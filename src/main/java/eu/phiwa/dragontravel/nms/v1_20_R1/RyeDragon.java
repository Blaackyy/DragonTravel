package eu.phiwa.dragontravel.nms.v1_20_R1;

import eu.phiwa.dragontravel.core.DragonTravel;
import eu.phiwa.dragontravel.core.hooks.server.IRyeDragon;
import eu.phiwa.dragontravel.core.movement.DragonType;
import eu.phiwa.dragontravel.core.movement.flight.Flight;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class RyeDragon extends EntityEnderDragon implements IRyeDragon {

    private final int wingCoolDown = 10;
    private final int travelY = DragonTravel.getInstance().getConfigHandler().getTravelHeight();

    private DragonType dragonType = DragonType.STATIONARY;

    private Player rider;

    // Source location
    private Location fromLoc;

    // Target location
    private Location toLoc;

    // Flight
    private Flight flight;
    private int currentWayPointIndex;

    // Travel
    private Location midLocA; // Middle location source world
    private Location midLocB; // Middle location target world
    private boolean finalMove = false;

    private double xPerTick;
    private double yPerTick;
    private double zPerTick;

    public RyeDragon(Location loc) {
        this(loc, ((CraftWorld) loc.getWorld()).getHandle());
    }

    public RyeDragon(Location loc, World notchWorld) {
        super(EntityTypes.C, notchWorld);
        //SetPos
        e(loc.getX(), loc.getY(), loc.getZ());
        float yaw = loc.getYaw() + 180;
        float pitch = 0f;

        while (yaw > 360)
            yaw -= 360;

        while (yaw < 0)
            yaw += 360;

        notchWorld.b(this);
        //notchWorld.addFreshEntity(this); IWorldWriter.class
    }

    public RyeDragon(World notchWorld) {
        //super(EntityTypes.x, notchWorld);
        super(EntityTypes.C, notchWorld);
    }


    /**
     * This method is a natural method of the Enderdragon extended by the RyeDragon.
     * It's fired when the dragon moves and fires the travel-method again to keep the dragon flying.
     */
    //public void tick() {
    @Override
    public void l() {
        if (getEntity() != null && rider != null) {
            if (getEntity().getPassenger() != null) {
                //getEntity().setPassenger(rider); //TODO: Reenable
            }

            rider.teleport(getEntity());
        }

        if (midLocA != null || toLoc != null) {
            Vector a = fromLoc.toVector();
            Vector b = midLocA != null ? midLocA.toVector() : toLoc.toVector();
            double distX = b.getX() - a.getX();
            double distY = b.getY() - a.getY();
            double distZ = b.getZ() - a.getZ();

            //vector trig functions have to be in rads...
            float yaw = 0f, pitch = (float) - Math.atan(distY / Math.sqrt(distX * distX + distZ * distZ));

            if (distX != 0) {
                if (distX < 0) {
                    yaw = (float) (1.5 * Math.PI);
                } else {
                    yaw = (float) (0.5 * Math.PI);
                }
                yaw = yaw - (float) Math.atan(distZ / distX);
            } else if (distZ < 0) {
                yaw = (float) Math.PI;
            }
            //back to degrees
            //setYawPitch(-yaw * 180F / (float) Math.PI - 180F, pitch * 180F / (float) Math.PI - 180F);
            a(-yaw * 180F / (float) Math.PI - 180F, pitch * 180F / (float) Math.PI - 180F);
        }

        switch (dragonType) {
            case LOC_TRAVEL:
            case HOME_TRAVEL:
            case FACTION_TRAVEL:
            case PLAYER_TRAVEL:
            case STATION_TRAVEL:
                travel();
                break;
            case MANNED_FLIGHT:
            case TIMED_FLIGHT:
                flight();
                break;
            default:
                break;
        }
    }

    //locX() = dn()
    //locY() = dp()
    //locZ() = dt()

    private void setX( double x ) {
        e( x, dp(), dt() );
    }
    private void setY( double y ) {
        e( dn(), y, dp() );
    }
    private void setZ( double z ) {
        e( dp(), dp(), z );
    }

    /**
     * Controls the dragon
     */
    @Override
    public void flight() {
        if ((int) dn() != flight.getWaypoints().get(currentWayPointIndex).getX())
            if (dn() < flight.getWaypoints().get(currentWayPointIndex).getX())
                setX( dn() + xPerTick );
            else
                setX( dn() - xPerTick );
        if ((int) dp() != flight.getWaypoints().get(currentWayPointIndex).getY())
            if ((int) dp() < flight.getWaypoints().get(currentWayPointIndex).getY())
                setY( dp() + yPerTick );
            else
                setY( dp() - yPerTick );
        if ((int) dt() != flight.getWaypoints().get(currentWayPointIndex).getZ())
            if (dt() < flight.getWaypoints().get(currentWayPointIndex).getZ())
                setZ( dt() + zPerTick );
            else
                setZ( dt() - zPerTick );

        if ((Math.abs((int) dt() - flight.getWaypoints().get(currentWayPointIndex).getZ()) <= 3) && Math.abs((int) dn() - flight.getWaypoints().get(currentWayPointIndex).getX()) <= 3 && (Math.abs((int) dp() - flight.getWaypoints().get(currentWayPointIndex).getY()) <= 5)) {
            if (currentWayPointIndex == flight.getWaypoints().size() - 1) {
                DragonTravel.getInstance().getDragonManager().removeRiderAndDragon(getEntity(), flight.getWaypoints().get(currentWayPointIndex).getAsLocation());
                return;
            }

            this.currentWayPointIndex++;

            this.fromLoc = getEntity().getLocation();
            this.toLoc = flight.getWaypoints().get(currentWayPointIndex).getAsLocation();

            if (!flight.getWaypoints().get(currentWayPointIndex).getWorldName().equals(this.getEntity().getWorld().getName())) {
                Location loc = flight.getWaypoints().get(currentWayPointIndex).getAsLocation();
                BlockPosition pos = new BlockPosition( loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() );
                // [1.15]       this.teleportTo(this.dimension, pos);
                // [1.16 (1)]   this.teleportTo(this.world.getDimensionManager(), pos);
                // [1.16 (2)]   this.teleportTo((WorldServer)this.world), pos);
                this.currentWayPointIndex++;
            }

            setMoveFlight();
        }
    }

    /**
     * Sets the x,y,z move for each tick
     */
    @Override
    public void setMoveFlight() {
        double distX = fromLoc.getX() - flight.getWaypoints().get(currentWayPointIndex).getX();
        double distY = fromLoc.getY() - flight.getWaypoints().get(currentWayPointIndex).getY();
        double distZ = fromLoc.getZ() - flight.getWaypoints().get(currentWayPointIndex).getZ();
        double tick = Math.sqrt((distX * distX) + (distY * distY)
                + (distZ * distZ)) / DragonTravel.getInstance().getConfigHandler().getSpeed();
        this.xPerTick = Math.abs(distX) / tick;
        this.yPerTick = Math.abs(distY) / tick;
        this.zPerTick = Math.abs(distZ) / tick;
    }

    /**
     * Starts the specified flight
     *
     * @param flight Flight to start
     */
    @Override
    public void startFlight(Flight flight, DragonType dragonType) {
        this.flight = flight;
        this.currentWayPointIndex = 0;
        this.dragonType = dragonType;

        this.toLoc = flight.getWaypoints().get(currentWayPointIndex).getAsLocation();
        this.fromLoc = getEntity().getLocation();

        setMoveFlight();
    }

    /**
     * Normal Travel
     */
    @Override
    public void travel() {
        if (getEntity().getPassenger() == null)
            return;

        double myX = dn();
        double myY = dp();
        double myZ = dt();

        if (finalMove) {
            // Go down to destination
            if ((int) dp() > (int) toLoc.getY())
                myY -= DragonTravel.getInstance().getConfigHandler().getSpeed();
                // Go up to destination
            else if ((int) dp() < (int) toLoc.getY())
                myY += DragonTravel.getInstance().getConfigHandler().getSpeed();
                // Reached destination
            else {
                // Interworld travel, dragon reached temporary destination in target world
                if (!getEntity().getWorld().getName().equals(toLoc.getWorld().getName())) {
                    this.rider = (Player) getEntity().getPassenger();
                    midLocB.getChunk().load();

                    Bukkit.getScheduler().runTaskLater(DragonTravel.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            DragonTravel.getInstance().getDragonManager().dismount(rider, true);
                            if (midLocB.getZ() < toLoc.getZ())
                                midLocB.setYaw((float) (-Math.toDegrees(Math.atan((midLocB.getX() - toLoc.getX()) / (midLocB.getZ() - toLoc.getZ())))));
                            else if (midLocB.getZ() > toLoc.getZ())
                                midLocB.setYaw((float) (-Math.toDegrees(Math.atan((midLocB.getX() - toLoc.getX()) / (midLocB.getZ() - toLoc.getZ())))) + 180.0F);
                            rider.teleport(midLocB);

                            if (!DragonTravel.getInstance().getDragonManager().mount(rider, false, dragonType)) return;
                            if (!DragonTravel.getInstance().getDragonManager().getRiderDragons().containsKey(rider)) return;

                            IRyeDragon dragon = DragonTravel.getInstance().getDragonManager().getRiderDragons().get(rider);
                            dragon.startTravel(toLoc, false, dragonType);
                            getEntity().remove();
                        }
                    }, 1L);
                }
                // Dragon reached final destination
                else {
                    DragonTravel.getInstance().getDragonManager().removeRiderAndDragon(getEntity(), true);
                    return;
                }
            }

            // Move player to new location on tick
            e(myX, myY, myZ);

            return;
        }

        if ((int) dp() < travelY)
            myY += DragonTravel.getInstance().getConfigHandler().getSpeed();

        if (myX < toLoc.getX())
            myX += xPerTick;
        else
            myX -= xPerTick;

        if (myZ < toLoc.getZ())
            myZ += zPerTick;
        else
            myZ -= zPerTick;

        // For higher travel speeds the accuracy for dismounts needs
        // to be decreased to prevent dragons from getting stuck
        double maxDiff = DragonTravel.getInstance().getConfigHandler().getSpeed() + 1;
        if (Math.abs(myZ - (int) toLoc.getZ()) <= maxDiff
                && Math.abs(myX - (int) toLoc.getX()) <= maxDiff) {
            finalMove = true;
        }

        e(myX, myY, myZ);
    }

    /**
     * ⒉setMoveTravel()->/dt travel
     * Sets the x,z move for each tick
     */
    @Override
    public void setMoveTravel() {
        double dist;
        double distX;
        double distY;
        double distZ;
        if (midLocA != null) {
            dist = fromLoc.distance(midLocA);
            distX = fromLoc.getX() - midLocA.getX();
            distY = fromLoc.getY() - midLocA.getY();
            distZ = fromLoc.getZ() - midLocA.getZ();
        } else {
            dist = fromLoc.distance(toLoc);
            distX = fromLoc.getX() - toLoc.getX();
            distY = fromLoc.getY() - toLoc.getY();
            distZ = fromLoc.getZ() - toLoc.getZ();
        }
        double tick = dist / DragonTravel.getInstance().getConfigHandler().getSpeed();
        xPerTick = Math.abs(distX) / tick;
        zPerTick = Math.abs(distZ) / tick;
        yPerTick = Math.abs(distY) / tick;
    }

    /**
     * ⒈startTravel()->/dt travel
     * Starts a travel to the specified location
     *
     * @param destLoc Location to start a travel to
     */
    @Override
    public void startTravel(Location destLoc, boolean interWorld, DragonType dragonType) {
        this.dragonType = dragonType;
        this.rider = (Player) getEntity().getPassenger();
        this.fromLoc = getEntity().getLocation();
        if (interWorld) {
            this.midLocA = new Location(getEntity().getWorld(), dn() + 50 + Math.random() * 100, travelY, dt() + 50 + Math.random() * 100);
            int scatter = 80;
            this.midLocB = destLoc.clone().add(scatter, scatter, scatter);
            this.toLoc = destLoc;
        } else {
            this.toLoc = destLoc;
        }
        setMoveTravel();
    }

    @Override
    public DragonType getDragonType() {
        return dragonType;
    }

    @Override
    public Entity getEntity() {
        if (getBukkitEntity() != null)
            return getBukkitEntity();
        return null;
    }

    /*@Override
    public boolean x() {
        return super.x();
    }*/

    public void fixWings() {
        // Artifact of old workaround for wings flipping really fast.
        // Method stub is required for interface.
    }

    public void setDragonType(DragonType dragonType) {
        this.dragonType = dragonType;
    }

    public int getWingCoolDown() {
        return wingCoolDown;
    }

    public Player getRider() {
        return rider;
    }

    public void setRider(Player rider) {
        this.rider = rider;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public int getCurrentWayPointIndex() {
        return currentWayPointIndex;
    }

    public void setCurrentWayPointIndex(int currentWayPointIndex) {
        this.currentWayPointIndex = currentWayPointIndex;
    }

    public boolean isFinalMove() {
        return finalMove;
    }

    public void setFinalMove(boolean finalMove) {
        this.finalMove = finalMove;
    }

    public int getTravelY() {
        return travelY;
    }

    public double getxPerTick() {
        return xPerTick;
    }

    public void setxPerTick(double xPerTick) {
        this.xPerTick = xPerTick;
    }

    public double getyPerTick() {
        return yPerTick;
    }

    public void setyPerTick(double yPerTick) {
        this.yPerTick = yPerTick;
    }

    public double getzPerTick() {
        return zPerTick;
    }

    public void setzPerTick(double zPerTick) {
        this.zPerTick = zPerTick;
    }

    @Override
    public boolean e_() {
        return super.e_();
    }

    @Override
    public boolean f_() {
        return super.f_();
    }

    private class WingFixerTask implements Runnable {

        private int id;
        private int cooldown;

        public void setId(int id) {
            this.id = id;
            this.cooldown = wingCoolDown;
        }

        @Override
        public void run() {
            cooldown -= 1;
            if (cooldown <= 0)
                Bukkit.getScheduler().cancelTask(id);
            final Location loc = getEntity().getLocation().add(0, 2, 0);
            final Material[] m = new Material[15];
            final MaterialData[] md = new MaterialData[15];

            int counter = 0;
            for (int y = 0; y <= 2; y++) {
                for (int x = -1; x <= 1; x++) {
                    m[counter] = loc.clone().add(x, -y, 0).getBlock().getType();
                    md[counter] = loc.clone().add(x, -y, 0).getBlock().getState().getData();
                    loc.clone().add(x, -y, 0).getBlock().setType(Material.BARRIER);
                    counter++;
                }

                for (int z = -1; z <= 1; z++) {
                    if (z == 0 && y == 0) continue;
                    m[counter] = loc.clone().add(0, -y, z).getBlock().getType();
                    md[counter] = loc.clone().add(0, -y, z).getBlock().getState().getData();
                    loc.clone().add(0, -y, z).getBlock().setType(Material.BARRIER);
                    counter++;
                }

                if (y == 0) {
                    loc.getBlock().setType(Material.WATER);
                }
                if (y == 1) {
                    loc.clone().add(0, -1, 0).getBlock().setType(Material.AIR);
                }
            }

            Bukkit.getScheduler().runTaskLater(DragonTravel.getInstance(), new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    for (int y = 0; y <= 2; y++) {
                        for (int x = -1; x <= 1; x++) {
                            loc.clone().add(x, -y, 0).getBlock().setType(m[count]);
                            loc.clone().add(x, -y, 0).getBlock().getState().setData(md[count]);
                            count++;
                        }
                        for (int z = -1; z <= 1; z++) {
                            if (z == 0) continue;
                            loc.clone().add(0, -y, z).getBlock().setType(m[count]);
                            loc.clone().add(0, -y, z).getBlock().getState().setData(md[count]);
                            count++;
                        }
                    }
                }
            }, 20L);
        }
    }

    @Override
    public void setCustomNameVisible(boolean b) {

    }

    // Old (until CB 1_12_R1), now only a compatibility-wrapper for our code
    public void setCustomDragonName(String name) {
        net.minecraft.network.chat.IChatBaseComponent nameInNewType = CraftChatMessage.fromStringOrNull(name); // convert from "name"
        if (nameInNewType == null) return;
        // Call new method
        setCustomName(nameInNewType);
    }

    // New (in CB 1_13_R1)
    public void setCustomName(net.minecraft.network.chat.IChatBaseComponent name) {
        super.a(name);
    }

    // Old (until CB 1_12_R1), now only a compatibility-wrapper for our code
    public String getCustomDragonName() {
        // Call new method
        net.minecraft.network.chat.IChatBaseComponent nameInNewType = getCustomName();
        return CraftChatMessage.fromComponent(nameInNewType);
    }

    // New (in CB 1_13_R1)
    public net.minecraft.network.chat.IChatBaseComponent getCustomName() {
        return super.ab();
    }

}
