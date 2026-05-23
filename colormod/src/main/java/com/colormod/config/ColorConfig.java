package com.colormod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class ColorConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("colormod.json");

    private static ColorConfig instance;

    // RGB values in range [0.0, 1.0]
    public float red   = 0.5f;
    public float green = 0.7f;
    public float blue  = 1.0f;
    // Overlay opacity [0.0, 1.0] — how strong the tint is
    public float opacity = 0.15f;

    public static ColorConfig get() {
        if (instance == null) load();
        return instance;
    }

    public static void load() {
        File file = CONFIG_PATH.toFile();
        if (file.exists()) {
            try (Reader r = new FileReader(file)) {
                ColorConfig loaded = GSON.fromJson(r, ColorConfig.class);
                instance = (loaded != null) ? loaded : new ColorConfig();
            } catch (IOException e) {
                instance = new ColorConfig();
            }
        } else {
            instance = new ColorConfig();
        }
    }

    public static void save() {
        try (Writer w = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(instance, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Returns ARGB int with given alpha (0–255). */
    public int toARGB(int alpha) {
        int r = clamp((int)(red   * 255));
        int g = clamp((int)(green * 255));
        int b = clamp((int)(blue  * 255));
        return (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    /** Returns ARGB int using stored opacity. */
    public int toARGB() {
        return toARGB(clamp((int)(opacity * 255)));
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
