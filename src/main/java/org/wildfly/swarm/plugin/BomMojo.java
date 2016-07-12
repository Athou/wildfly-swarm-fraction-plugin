/*
 * Copyright 2016 Red Hat, Inc, and individual contributors.
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate-bom",
        defaultPhase = LifecyclePhase.PACKAGE)
public class BomMojo extends AbstractFractionsMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Function<MavenProject,Boolean> filter = all();
        if ( this.stabilityIndex != null ) {
            this.stabilityIndex = this.stabilityIndex.trim();

            if ( this.stabilityIndex.equals( "*" ) ) {
                filter = all();
            } else if ( this.stabilityIndex.endsWith("+") ) {
                int level = Integer.parseInt( this.stabilityIndex.substring(0, this.stabilityIndex.length()-1));
                filter = isAtLeast( level );
            } else {
                int level = Integer.parseInt( this.stabilityIndex );
                filter = isEqualTo( Integer.parseInt( this.stabilityIndex ) );
            }
        }
        List<MavenProject> fractions = fractions(filter);
        final Path bomPath = Paths.get(this.project.getBuild().getOutputDirectory(), "bom.pom");
        try {
            Files.createDirectories( bomPath.getParent() );
            Files.write(bomPath,
                        BomBuilder.generateBOM(this.project, readTemplate(), fractions).getBytes());
        } catch (IOException e) {
            throw new MojoFailureException("Failed to write bom.pom", e);
        }

        getLog().info(String.format("Wrote bom to %s", bomPath));

        for (MavenProject fraction : fractions) {
            getLog().info(String.format("%40s", fraction.getGroupId() +":" + fraction.getArtifactId()));
        }

        project.setFile( bomPath.toFile() );
    }

    protected String readTemplate() throws MojoFailureException {
        if (this.template == null) {

            throw new MojoFailureException("No template specified");
        }
        try {

            return new String(Files.readAllBytes(this.template.toPath()), "UTF-8");
        } catch (IOException e) {

            throw new MojoFailureException("Failed to read template " + this.template, e);
        }
    }

    @Parameter
    private String stabilityIndex;

    @Parameter
    private File template;

}
