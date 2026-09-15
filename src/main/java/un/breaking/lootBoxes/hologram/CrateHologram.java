//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package un.breaking.lootBoxes.hologram;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import un.breaking.lootBoxes.LootCrates;
import un.breaking.lootBoxes.models.CustomCrate;

public class CrateHologram {
    private final Location blockLocation;
    private final String crateId;
    private final List<ArmorStand> armorStands;
    private BukkitTask particleTask;
    private BukkitTask floatTask;

    public CrateHologram(Location blockLocation, String crateId, List<ArmorStand> armorStands) {
        this.blockLocation = blockLocation;
        this.crateId = crateId;
        this.armorStands = armorStands;
    }

    public void startParticles(un.breaking.lootBoxes.LootCrates plugin, final un.breaking.lootBoxes.models.CustomCrate crate) {
        if (this.particleTask != null) {
            this.particleTask.cancel();
        }

        if (this.floatTask != null) {
            this.floatTask.cancel();
        }

        if (crate.isFloatingAnimation()) {
            this.floatTask = (new BukkitRunnable() {
                double offset = (double)0.0F;

                public void run() {
                    if (CrateHologram.this.blockLocation.getWorld() == null) {
                        this.cancel();
                    } else {
                        double floatOffset = Math.sin(this.offset) * 0.1;
                        Location base = CrateHologram.this.blockLocation.clone().add((double)0.5F, (double)2.5F + floatOffset, (double)0.5F);

                        for(int i = 0; i < CrateHologram.this.armorStands.size(); ++i) {
                            ArmorStand stand = (ArmorStand)CrateHologram.this.armorStands.get(i);
                            if (stand != null && !stand.isDead()) {
                                Location newLoc = base.clone().subtract((double)0.0F, (double)i * 0.3, (double)0.0F);
                                stand.teleport(newLoc);
                            }
                        }

                        this.offset += 0.1;
                    }
                }
            }).runTaskTimer(plugin, 0L, 2L);
        }

        this.particleTask = (new BukkitRunnable() {
            double angle = (double)0.0F;

            public void run() {
                if (CrateHologram.this.blockLocation.getWorld() == null) {
                    this.cancel();
                } else if (!CrateHologram.this.blockLocation.getWorld().getNearbyEntities(CrateHologram.this.blockLocation, (double)30.0F, (double)30.0F, (double)30.0F).isEmpty()) {
                    Location center = CrateHologram.this.blockLocation.clone().add((double)0.5F, 1.2, (double)0.5F);
                    double radius = crate.getParticleRadius();
                    int count = crate.getParticleCount();

                    for(int i = 0; i < count; ++i) {
                        double offsetAngle = this.angle + (double)i * ((Math.PI * 2D) / (double)count);
                        double x = Math.cos(offsetAngle) * radius;
                        double z = Math.sin(offsetAngle) * radius;
                        Location particleLoc = center.clone().add(x, (double)0.0F, z);
                        Particle particle = crate.getParticle();
                        if (particle == Particle.DUST) {
                            Particle.DustOptions dust = new Particle.DustOptions(crate.getParticleColor(), 1.0F);
                            CrateHologram.this.blockLocation.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, dust);
                        } else {
                            CrateHologram.this.blockLocation.getWorld().spawnParticle(particle, particleLoc, 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
                        }
                    }

                    if (this.angle % (double)1.0F < 0.2) {
                        double rx = (Math.random() - (double)0.5F) * 0.6;
                        double rz = (Math.random() - (double)0.5F) * 0.6;
                        Location floatLoc = center.clone().add(rx, (double)-0.5F, rz);
                        Particle.DustOptions dust = new Particle.DustOptions(crate.getParticleColor(), 0.8F);
                        CrateHologram.this.blockLocation.getWorld().spawnParticle(Particle.DUST, floatLoc, 1, (double)0.0F, 0.15, (double)0.0F, 0.05, dust);
                    }

                    if (crate.getParticleCount() > 2) {
                        double helixY = this.angle % (Math.PI * 2D) / (Math.PI * 2D) * (double)2.0F;
                        double helixX = Math.cos(this.angle * (double)2.0F) * 0.4;
                        double helixZ = Math.sin(this.angle * (double)2.0F) * 0.4;
                        Location helixLoc = center.clone().add(helixX, helixY, helixZ);
                        Particle.DustOptions dust = new Particle.DustOptions(crate.getParticleColor(), 0.6F);
                        CrateHologram.this.blockLocation.getWorld().spawnParticle(Particle.DUST, helixLoc, 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F, dust);
                    }

                    this.angle += 0.15;
                }
            }
        }).runTaskTimer(plugin, 0L, 2L);
    }

    public void remove() {
        if (this.particleTask != null) {
            this.particleTask.cancel();
            this.particleTask = null;
        }

        if (this.floatTask != null) {
            this.floatTask.cancel();
            this.floatTask = null;
        }

        for(ArmorStand stand : this.armorStands) {
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
        }

        this.armorStands.clear();
    }

    public Location getBlockLocation() {
        return this.blockLocation;
    }

    public String getCrateId() {
        return this.crateId;
    }

    public List<ArmorStand> getArmorStands() {
        return this.armorStands;
    }
}
