package vazkii.playerschoice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import ibxm.Player;

public class ModSettings {

	private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
	
	public ModConfig[] mods;
	private File in, out;
	
	public ModSettings(File in, File out) {
		this.in = in;
		this.out = out;
		
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
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(out))) {
			for(ModConfig m : mods) {
				writer.write(m.id);
				writer.write("=");
				writer.write(Boolean.toString(m.enabled));
				writer.newLine();
			}
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
		public boolean base;
		
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
