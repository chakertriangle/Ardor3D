/**
 * Copyright (c) 2008-2019 Bird Dog Games, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package com.ardor3d.util;

import java.util.HashMap;
import java.util.Map;

import com.ardor3d.renderer.material.fog.FogParams;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

public abstract class MaterialUtil {

    private MaterialUtil() {}

    public static void autoMaterials(final Spatial spat) {
        autoMaterials(spat, false);
    }

    public static void autoMaterials(final Spatial spat, final boolean replaceExisting) {
        if (spat != null) {
            spat.updateWorldRenderStates(true);
        }

        final String material = processSpatialMaterial(spat, replaceExisting);

        if (material != null) {
            spat.setRenderMaterial(material);
        }
    }

    private static String processSpatialMaterial(final Spatial spat, final boolean replaceExisting) {
        String material = null;
        if (spat instanceof Node) {
            material = processNodeMaterial((Node) spat, replaceExisting);
        } else if (replaceExisting || spat.getRenderMaterial() == null) {
            if (spat instanceof Mesh) {
                material = calcMeshMaterial((Mesh) spat);
            }
        }
        return material;
    }

    private static String processNodeMaterial(final Node node, final boolean replaceExisting) {
        // Check if we have any children at all...
        if (node.getNumberOfChildren() == 0) {
            return null;
        }

        // Get and track the material for each of our kids
        final Map<Spatial, String> kidMaterials = new HashMap<>();
        final Map<String, Integer> materialCount = new HashMap<>();
        node.getChildren().forEach(child -> {
            final String material = processSpatialMaterial(child, replaceExisting);
            if (material != null) {
                kidMaterials.put(child, material);
                if (!materialCount.containsKey(material)) {
                    materialCount.put(material, 1);
                } else {
                    materialCount.put(material, materialCount.get(material) + 1);
                }
            }
        });

        if (materialCount.size() == 0) {
            return null;
        }

        // If only 1 material across children, return it
        if (materialCount.size() == 1) {
            return materialCount.entrySet().iterator().next().getKey();
        }

        // otherwise, grab the highest used material...
        String highEntry = null;
        int highCount = -1;
        for (final Map.Entry<String, Integer> entry : materialCount.entrySet()) {
            if (entry.getValue() > highCount) {
                highEntry = entry.getKey();
                highCount = entry.getValue();
            }
        }

        // Walk through our kids again and set any that don't match mostUsed.
        final String mostUsed = highEntry;
        node.getChildren().forEach(child -> {
            final String material = kidMaterials.get(child);
            if (!mostUsed.equals(material)) {
                child.setRenderMaterial(material);
            }
        });

        // return our most used material to be set higher in the graph
        return mostUsed;
    }

    private static String calcMeshMaterial(final Mesh mesh) {
        final StringBuilder material = new StringBuilder();
        // First, check if we are lit or not
        final LightState ls = mesh.getWorldRenderState(StateType.Light);
        final boolean lit = (ls != null && ls.isEnabled() && ls.count() > 0);
        material.append(lit ? "lit/" : "unlit/");

        // Now check if we are textured
        final TextureState ts = mesh.getWorldRenderState(StateType.Texture);
        final boolean textured = (ts != null && ts.isEnabled() && ts.getNumberOfSetTextures() > 0);
        material.append(textured ? "textured/" : "untextured/");

        // Check if we are using vertex colors
        final boolean vertColor = mesh.getMeshData().containsKey(MeshData.KEY_ColorCoords);
        material.append(vertColor ? "vertex_color" : "basic");
        material.append(lit ? "_phong" : "");

        // Finally, check if we are using fog
        final boolean foggy = mesh.hasProperty(FogParams.DefaultPropertyKey);
        material.append(foggy ? "_fog" : "");

        material.append(".yaml");

        System.out.println(material + " - " + mesh.getName());

        return material.toString();
    }
}
