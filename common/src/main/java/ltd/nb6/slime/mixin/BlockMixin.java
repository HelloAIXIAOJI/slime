package ltd.nb6.slime.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "stepOn", at = @At("HEAD"), cancellable = true)
    private void slime$stepOn(Level level, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        double d = Math.abs(entity.getDeltaMovement().y);
        if (d < 0.1 && !entity.isSteppingCarefully()) {
            double e = 0.4 + d * 0.2;
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(e, 1.0, e));
        }
    }

    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    private void slime$fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (!entity.isSuppressingBounce()) {
            entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
            ci.cancel();
        }
    }

    @Inject(method = "updateEntityAfterFallOn", at = @At("HEAD"), cancellable = true)
    private void slime$updateEntityAfterFallOn(net.minecraft.world.level.BlockGetter level, Entity entity, CallbackInfo ci) {
        if (!entity.isSuppressingBounce()) {
            this.slime$bounce(entity);
            ci.cancel();
        }
    }

    @Unique
    private void slime$bounce(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.y < 0.0) {
            double d = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setDeltaMovement(vec3.x, -vec3.y * d, vec3.z);
        }
    }
}
