package dev.agnor99.better_portal_opening;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber
public class PortalOpener {

    @SubscribeEvent
    public static void onArrowImpact(ProjectileImpactEvent event) {

        if (event.getProjectile() instanceof AbstractArrow arrow
            && arrow.isOnFire()
            && event.getRayTraceResult() instanceof BlockHitResult hitResult
            && createPortal(arrow.level, hitResult.getBlockPos().relative(hitResult.getDirection()))) {
            if (arrow instanceof Arrow || arrow instanceof SpectralArrow)
                arrow.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    @SubscribeEvent
    public static void onSwordSwing(PlayerInteractEvent.LeftClickBlock event) {
        if (validWorld(event.getWorld())) {
            ItemStack stack = event.getItemStack();
            if (!stack.isEmpty()
                && stack.getItem() != Items.ENCHANTED_BOOK
                && stack.isEnchanted()
                && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, stack) != 0
                && createPortal(event.getWorld(), event.getPos().relative(event.getFace()))
                && stack.isDamageableItem()) {

                stack.hurtAndBreak(1,
                            event.getPlayer(),
                            player -> player.broadcastBreakEvent(event.getHand())
                    );

            }
        }
    }


    private static boolean createPortal(Level world, BlockPos pos) {
        if (validWorld(world)) {
            Optional<PortalShape> optional = PortalShape.findEmptyPortalShape(world, pos, Direction.Axis.X);
            optional = ForgeEventFactory.onTrySpawnPortal(world, pos, optional);
            if (optional.isPresent()) {
                optional.get().createPortalBlocks();
                return true;
            }
        }
        return false;
    }

    private static boolean validWorld(Level world){
        return !world.isClientSide() && world.dimension() == Level.OVERWORLD || world.dimension() == Level.NETHER;
    }
}