package openblocks;

import java.util.Map;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

//must be higher than one in openmodslib
@SortingIndex(32)
public class OpenBlocksCorePlugin implements IFMLLoadingPlugin {

	public static Logger log;
	static {
		log = Logger.getLogger("OpenBlocksCore");
		log.setParent(FMLLog.getLogger());
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "openblocks.asm.OpenBlocksClassTransformer" };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {}

	@Deprecated
	public String[] getLibraryRequestClass() {
		return null;
	}

}
