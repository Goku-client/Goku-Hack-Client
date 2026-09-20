package com.research.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    public static Config I = new Config();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // ESP
    public boolean esp = true;
    public boolean tracers = true;
    public boolean boxes = true;
    public boolean names = true;
    public boolean tracerFromCenter = false;
    public boolean espPlayers = true;
    public boolean espHostile = true;
    public boolean espPassive = false;
    public double espRange = 64;

    // Aim
    public boolean aim = false;
    public boolean aimPlayers = true;
    public boolean aimMobs = true;
    public boolean aimHead = true;
    public boolean aimVisibleOnly = true;
    public double aimRange = 20;
    public double aimFov = 90;
    public double aimSmooth = 0.35; // 0.05 slow .. 1 instant

    static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("research_client.json");
    }

    public static void load() {
        try {
            if (Files.exists(path())) I = GSON.fromJson(Files.readString(path()), Config.class);
        } catch (Exception e) { I = new Config(); }
    }

    public static void save() {
        try { Files.writeString(path(), GSON.toJson(I)); } catch (Exception ignored) {}
    }
  }
