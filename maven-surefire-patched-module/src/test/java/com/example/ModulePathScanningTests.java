/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @author Sam Brannen
 */
class ModulePathScanningTests {

	/**
	 * This test actually asserts the behavior for class path scanning in a
	 * patched module, in order to demonstrate that we need to scan the module
	 * path to find everything.
	 */
	@Test
	void classLoader_getResources_for_Class() throws Exception {
		List<URL> resourceUrls = Collections.list(getClass().getClassLoader().getResources("com/example"));
		assertClassPathResources(resourceUrls, "Resources for Class");
	}

	/**
	 * This test actually asserts the behavior for class path scanning in a
	 * patched module, in order to demonstrate that we need to scan the module
	 * path to find everything.
	 */
	@Test
	void classLoader_getResources_for_Module() throws Exception {
		List<URL> resourceUrls = Collections.list(getClass().getModule().getClassLoader().getResources("com/example"));
		assertClassPathResources(resourceUrls, "Resources for Module");
	}

	private void assertClassPathResources(List<URL> resourceUrls, String description) {
		// We do not assert that the size is 1, since the behavior may be different
		// in the IDE vs. when run via Maven Surefire. Specifically, when run with
		// Maven Surefire the ClassLoader finds target-classes/com/example twice,
		// once with a trailing slash and once without a trailing slash.
		// assertThat(resourceUrls).hasSize(1);

		// Only test classes and test resources are found via the ClassLoader APIs in a
		// patched module.
		assertThat(resourceUrls).map(URL::getPath).as(description).allMatch(path -> path.contains("/test-classes"));
	}

	@ParameterizedTest
	// Test with and without leading slash in resource name.
	@ValueSource(strings = { "classpath*:com/example/**/*.class", "classpath*:/com/example/**/*.class" })
	void pathMatchingResourcePatternResolver(String locationPattern) throws Exception {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources(locationPattern);
		List<String> paths = Arrays.stream(resources).map(this::toUri).map(URI::getPath).toList();

		assertThat(paths).hasSize(5);
		// There should be 3 classes from src/main/java.
		assertThat(paths).filteredOn(path -> path.contains("/classes/")).hasSize(3);
		// There should be 2 classes from src/test/java.
		assertThat(paths).filteredOn(path -> path.contains("/test-classes/")).hasSize(2);
	}

	private URI toUri(Resource resource) {
		try {
			return resource.getURI();
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

}
