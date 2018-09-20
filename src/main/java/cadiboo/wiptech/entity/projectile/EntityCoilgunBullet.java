package cadiboo.wiptech.entity.projectile;

import cadiboo.wiptech.WIPTech;
import cadiboo.wiptech.entity.IModEntity;
import cadiboo.wiptech.util.ModDamageSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityCoilgunBullet extends EntityThrowable implements IModEntity {

	public EntityCoilgunBullet(final World worldIn) {
		super(worldIn);
	}

	public EntityCoilgunBullet(final World world, final EntityPlayer player) {
		super(world, player);
	}

	@Override
	protected void onImpact(final RayTraceResult result) {
		if (this.world.isRemote) {
			return;
		}
		WIPTech.info("EntityCoilgunBullet#onImpact");
		if (result == null) {
			return;
		}
		if (result.entityHit != null) {
//				result.entityHit.setDead(); //this completely breaks the game bahaha
			result.entityHit.attackEntityFrom(ModDamageSource.causeCoilgunBulletDamage(this, this.getThrower()), 10);
		}

	}

}