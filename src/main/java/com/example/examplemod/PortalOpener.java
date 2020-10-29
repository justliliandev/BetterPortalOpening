package com.example.examplemod;

import net.minecraft.block.PortalSize;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber
public class PortalOpener {

    @SubscribeEvent
    public static void onArrowImpact(ProjectileImpactEvent.Arrow arrowImpactEvent) {
        if(! (arrowImpactEvent.getArrow() instanceof ArrowEntity)) {
            return;
        }
        ArrowEntity arrow = (ArrowEntity) arrowImpactEvent.getArrow();
        if(! arrow.isBurning()) {
            return;
        }
        if(! arrowImpactEvent.getRayTraceResult().getType().equals(RayTraceResult.Type.BLOCK)){
            return;
        }
        BlockRayTraceResult rayTraceResult = (BlockRayTraceResult) arrowImpactEvent.getRayTraceResult();
        BlockPos pos = rayTraceResult.getPos().offset(rayTraceResult.getFace(), 1);

        if(createPortal(arrow.world, pos)) {
            arrow.remove();
        }
    }

    @SubscribeEvent
    public static void onSwordSwing(PlayerInteractEvent.LeftClickBlock leftClickBlockEvent) {
        if(! validWorld(leftClickBlockEvent.getPlayer().world)) {
            return;
        }
        ItemStack itemStack = leftClickBlockEvent.getItemStack();
        if(itemStack.isEmpty() || !itemStack.isEnchanted()) {
            return;
        }
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
        if(! enchantments.containsKey(Enchantments.FIRE_ASPECT)) {
           return;
        }
        if(createPortal(leftClickBlockEvent.getWorld(), leftClickBlockEvent.getPos().offset(leftClickBlockEvent.getFace()))){
            if(itemStack.isDamageable()) {
                itemStack.damageItem(1, leftClickBlockEvent.getPlayer(),
                        player -> player.sendBreakAnimation(leftClickBlockEvent.getHand())
                );
            }
        }
    }


    private static boolean createPortal(World world, BlockPos pos) {
        if (validWorld(world)) {
            Optional<PortalSize> optional = PortalSize.func_242964_a(world, pos, Direction.Axis.X);
            optional = ForgeEventFactory.onTrySpawnPortal(world, pos, optional);
            if (optional.isPresent()) {
                optional.get().placePortalBlocks();
                return true;
            }
        }
        return false;
    }
    private static boolean validWorld(World world){
        return !world.isRemote() && (world.func_234923_W_() == World.field_234918_g_ || world.func_234923_W_() == World.field_234919_h_);
    }
}
