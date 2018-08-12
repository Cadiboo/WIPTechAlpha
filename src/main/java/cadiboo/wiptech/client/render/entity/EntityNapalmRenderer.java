package cadiboo.wiptech.client.render.entity;

import cadiboo.wiptech.client.ClientUtil;
import cadiboo.wiptech.entity.projectile.EntityNapalm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class EntityNapalmRenderer extends Render<EntityNapalm> {

    public EntityNapalmRenderer(RenderManager renderManager) {
	super(renderManager);
    }

    @Override
    public void doRender(EntityNapalm entity, double x, double y, double z, float entityYaw, float partialTicks) {
	super.doRender(entity, x, y, z, entityYaw, partialTicks);

	ClientUtil.enableMaxLighting();

	GlStateManager.disableLighting();

	GlStateManager.pushMatrix();
	try {
	    GlStateManager.translate(x, y + 0.125, z);

	    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

	    bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

	    TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();

	    TextureAtlasSprite lava = texturemap.getAtlasSprite("minecraft:blocks/lava_flow");

	    RenderHelper.enableGUIStandardItemLighting();

	    ClientUtil.drawSeamlessCuboid(lava.getMinU(), lava.getMaxU(), lava.getMinV(), lava.getMaxV(), 0.125, 0.125,
		    0.125, 1);
//	    entity.setFire(1);
	} catch (Exception e) {
	    // TODO: handle exception
	}
	GlStateManager.popMatrix();

    }

    @Override
    protected ResourceLocation getEntityTexture(EntityNapalm entity) {
	return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

}