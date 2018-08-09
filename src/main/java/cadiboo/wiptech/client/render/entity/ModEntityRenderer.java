package cadiboo.wiptech.client.render.entity;

import cadiboo.wiptech.entity.ModEntity;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public abstract class ModEntityRenderer<ME extends ModEntity> extends Render<ME> {

	protected ModEntityRenderer(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(ME entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}

}