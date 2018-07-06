package vazkii.playerschoice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

public class ModSettings {

	private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
	
	public ModConfig[] mods;
	private final File in, out;
	private final File instanceDir, filesDir;
	
	public ModSettings(File in, File out, File instanceDir, File filesDir) {
		this.in = in;
		this.out = out;
		this.instanceDir = instanceDir;
		this.filesDir = filesDir;
		
		if(!filesDir.exists())
			filesDir.mkdir();
		
		read();
	}
	
	public void read() {
		try {
			if(in.exists()) 
				mods = PRETTY_GSON.fromJson(new FileReader(in), ModConfig[].class);
		} catch(IOException e) {}
		
		if(mods == null) {
			ModConfig example = new ModConfig();
			example.id = "example";
			example.name = "Example Mod";
			example.desc = "This is the example for a mod template. id is the mod ID, which needs to be set properly. name and desc are just for display in the GUI. website is optional for a link to the mod's website. base is whether it should come enabled by default or not.";
			example.website = "https://vazkii.us/";
			example.base = true;
			
			mods = new ModConfig[] { example };
			write();
		}
		
		for(ModConfig m : mods)
			m.enabled = m.base;
	}
	
	public void write() {
		try (JsonWriter writer = PRETTY_GSON.newJsonWriter(new FileWriter(in))) {
			PRETTY_GSON.toJson(mods, ModConfig[].class, writer);
		} catch(IOException e) {}
	}
	
	public void commit() {
		Map<String, Boolean> modSettings = new HashMap<>();
		Map<String, Boolean> copyFiles = new HashMap<>();
		for(ModConfig m : mods) {
			putMod(modSettings, m.id, m.enabled);
			
			if(m.extraMods != null)
				for(String s : m.extraMods)
					putMod(modSettings, s, m.enabled);
			
			if(m.enabled && m.copyFiles != null)
				for(String s : m.copyFiles)
					copyFiles.put(s, m.copyContents);
		}
		
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(out))) {
			for(String s : modSettings.keySet()) {
				writer.write(s);
				writer.write("=");
				writer.write(Boolean.toString(modSettings.get(s)));
				writer.newLine();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		for(String s : copyFiles.keySet()) {
			Boolean copyContents = copyFiles.get(s);
			File file = new File(filesDir, s);
			if (copyContents)
				for (String child : file.list())
					copyFileOrDirectory(new File(file, child), new File(instanceDir, child));
			else
				copyFileOrDirectory(file, new File(instanceDir, s));
		}
	}
	
	private void putMod(Map<String, Boolean> map, String mod, boolean enabled) {
		if(mod == null || mod.isEmpty())
			return;
		
		if(map.containsKey(mod)) {
			boolean curr = map.get(mod);
			if(!curr && enabled)
				map.put(mod, enabled);
		} else map.put(mod, enabled);	
	}

	private void copyFileOrDirectory(File file, File target) {
		try {
			if(file.isDirectory())
				FileUtils.copyDirectory(file, target);
			else FileUtils.copyFile(file, target);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class ModConfig implements Comparable<ModConfig> {
		
		public String id;
		public String name;
		public String desc;
		public String website;
		public String category = "";
		public String group = "";
		public boolean base;
		
		public String[] extraMods = new String[0];
		public String[] copyFiles = new String[0];
		public boolean copyContents = false;
		
		public transient boolean enabled;

		@Override
		public int compareTo(ModConfig o) {
			if(category.isEmpty() && !o.category.isEmpty())
				return 1;
			
			int diff = category.compareTo(o.category);
			if(diff == 0)
				return name.compareTo(o.name);
			return diff;
		}
		
	}
	
}
