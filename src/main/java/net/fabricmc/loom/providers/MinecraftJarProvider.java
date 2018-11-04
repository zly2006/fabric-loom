/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, 2017, 2018 FabricMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.fabricmc.loom.providers;

import net.fabricmc.loom.LoomGradleExtension;
import net.fabricmc.loom.util.MapJarsTiny;
import net.fabricmc.stitch.merge.JarMerger;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class MinecraftJarProvider {

	public File MINECRAFT_INTERMEDIARY_JAR;
	public File MINECRAFT_MERGED_JAR;
	public File MINECRAFT_MAPPED_JAR;

	MinecraftProvider minecraftProvider;

	public MinecraftJarProvider(Project project, MinecraftProvider minecraftProvider) throws IOException {
		this.minecraftProvider = minecraftProvider;
		initFiles(project, minecraftProvider);
		proccess(project, minecraftProvider);
	}

	private void proccess(Project project, MinecraftProvider minecraftProvider) throws IOException {
		if (!MINECRAFT_MERGED_JAR.exists()) {
			mergeJars(project);
		}

		if (getMappedJar().exists()) {
			getMappedJar().delete();
		}
		if (getIntermediaryJar().exists()) {
			getIntermediaryJar().delete();
		}
		new MapJarsTiny().mapJars(this, project);

		if (!MINECRAFT_MAPPED_JAR.exists()) {
			throw new RuntimeException("mapped jar not found");
		}
		minecraftProvider.addDep(MINECRAFT_MAPPED_JAR, project);
	}

	public void mergeJars(Project project) throws IOException {
		project.getLogger().lifecycle(":merging jars");
		FileInputStream client = new FileInputStream(minecraftProvider.MINECRAFT_CLIENT_JAR);
		FileInputStream server = new FileInputStream(minecraftProvider.MINECRAFT_SERVER_JAR);
		FileOutputStream merged = new FileOutputStream(MINECRAFT_MERGED_JAR);

		JarMerger jarMerger = new JarMerger(client, server, merged);

		jarMerger.merge();
		jarMerger.close();

		client.close();
		server.close();
		merged.close();
	}

	private void initFiles(Project project, MinecraftProvider minecraftProvider) {
		LoomGradleExtension extension = project.getExtensions().getByType(LoomGradleExtension.class);
		MINECRAFT_INTERMEDIARY_JAR = new File(extension.getUserCache(), minecraftProvider.minecraftVersion + "-intermediary.jar");
		MINECRAFT_MERGED_JAR = new File(extension.getUserCache(), minecraftProvider.minecraftVersion + "-merged.jar");
		MINECRAFT_MAPPED_JAR = new File(extension.getUserCache(), minecraftProvider.minecraftVersion + "-mapped-" + minecraftProvider.pomfVersion + ".jar");

	}

	public File getMappingFile() {
		return minecraftProvider.pomfProvider.MAPPINGS_TINY;
	}

	public Collection<File> getMapperPaths() {
		return minecraftProvider.libraryProvider.getLibraries();
	}

	public File getInputJar() {
		return MINECRAFT_MERGED_JAR;
	}

	public File getIntermediaryJar() {
		return MINECRAFT_INTERMEDIARY_JAR;
	}

	public File getMappedJar() {
		return MINECRAFT_MAPPED_JAR;
	}

}
