/**
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.swarm.plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Bob McWhirter
 */
public class Fraction {

    public Fraction(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public String getArtifactId() {
        return this.artifactId;
    }

    public String getVersion() {
        return this.version;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addDependency(Fraction fraction) {
        this.dependencies.add(fraction);
    }

    public Set<Fraction> getDependencies() {
        return this.dependencies;
    }

    public String toString() {
        String artifactId = this.artifactId;
        if ( artifactId.endsWith( "-api" ) ) {
            artifactId = artifactId.substring( 0, artifactId.length() - 4 );
        }
        return this.groupId + ":" + artifactId + ":" + this.version;
    }

    @JsonIgnore
    public String getDependenciesString() {
        return String.join(", ", this.dependencies.stream().map(e -> e.toString())
                .collect(Collectors.toList()));
    }

    private final String groupId;

    private final String artifactId;

    private final String version;

    private String description;

    private final Set<Fraction> dependencies = new HashSet<>();
}
