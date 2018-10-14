package cadiboo.wiptech.util.resourcelocation;

import net.minecraft.util.ResourceLocation;

public class ModResourceLocation extends ResourceLocation {

	public ModResourceLocation(final ModResourceLocationDomain resourceDomain, final ModResourceLocationPath resourcePath) {
		super(resourceDomain.toString(), resourcePath.toString());
	}

	public ModResourceLocation(final String resourceDomain, final String resourcePath) {
		this(new ModResourceLocationDomain(resourceDomain), new ModResourceLocationPath(resourcePath));
	}

	public ModResourceLocation(final ResourceLocation resourceLocation) {
		this(new ModResourceLocationDomain(resourceLocation.getResourceDomain()).toString(), new ModResourceLocationPath(resourceLocation.getResourcePath()).toString());
	}

	protected ModResourceLocation(final String... resourceName) {
		this(new ModResourceLocationDomain(resourceName[0]).toString(), new ModResourceLocationPath(resourceName[1]).toString());
	}

	public ModResourceLocation(final String resourceLocation) {
		this(splitObjectName(resourceLocation));
	}

	@Override
	public String toString() {
		if (this.resourceDomain.equals("")) {
			return this.resourcePath;
		}
		return super.toString();
	}

}