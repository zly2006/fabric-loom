/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2024 FabricMC
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

package net.fabricmc.loom.test.unit

import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.internal.catalog.DelegatingProjectDependency
import org.gradle.api.internal.project.ProjectInternal
import spock.lang.Specification

import net.fabricmc.loom.util.gradle.GradleUtils

class GradleUtilsTest extends Specification {
	def "get default project dependency"() {
		given:
		def project = Mock(ProjectInternal)
		def dependency = new DefaultProjectDependency(project, false)

		when:
		def result = GradleUtils.getDependencyProject(dependency)

		then:
		result == project
	}

	def "get delegated project dependency"() {
		given:
		def project = Mock(ProjectInternal)
		def dependency = new DefaultProjectDependency(project, true)
		def delegate = new DelegatingProjectDependency(null, dependency)

		when:
		def result = GradleUtils.getDependencyProject(delegate)

		then:
		result == project
	}
}
