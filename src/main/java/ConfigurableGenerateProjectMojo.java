/**
 * Copyright © 2018 Mayo Clinic (RSTKNOWLEDGEMGMT@mayo.edu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.maven.archetype.common.Constants;
import org.apache.maven.archetype.mojos.CreateProjectFromArchetypeMojo;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "generate", requiresProject = false)
@Execute(phase = LifecyclePhase.GENERATE_SOURCES)
public class ConfigurableGenerateProjectMojo extends CreateProjectFromArchetypeMojo {

  @Parameter( required = true )
  private File propertyFile;

  @Parameter( defaultValue = "${basedir}", readonly = true )
  private File baseDir;

  @Parameter( defaultValue = "${session}", readonly = true )
  private MavenSession sessionRef;

  /**
   * If true, deletes the content of the target directory where the archetype will
   * be generated before the generation process starts
   */
  @Parameter( defaultValue = "true", required = false)
  private boolean clear;

  /**
   * If true, prevents the archetype plugin from registering the new project as a child module of a
   * parent POM in baseDir. In this case, the parent POM is responsible for registering the children as appropriate
   */
  @Parameter( defaultValue = "false" )
  private boolean preserveRootPOM;

  public void execute()
      throws MojoExecutionException, MojoFailureException {


    if (propertyFile != null && propertyFile.exists()) {
      try {

        Properties prp = new Properties();
        prp.load(new FileInputStream(propertyFile));
        sessionRef.getUserProperties().putAll(prp);


        File targetDir = new File( baseDir, prp.getProperty("artifactId"));
        File basedirPom = new File( baseDir, Constants.ARCHETYPE_POM );
        byte[] originalPom = new byte[0];

        if (preserveRootPOM && basedirPom.exists()) {
          originalPom = FileUtils.readFileToByteArray(basedirPom);
        }

        if (clear && targetDir.exists()) {
          FileUtils.deleteDirectory(targetDir);
        }

        super.execute();

        if (preserveRootPOM && originalPom.length > 0) {
          FileUtils.writeByteArrayToFile(basedirPom,originalPom);
        }

      } catch (Exception e) {
        throw new MojoExecutionException( e.getMessage(), e);
      }

    } else {
      throw new MojoFailureException( "Property file <" + propertyFile + "> could not be resolved to a .properties file" );
    }


  }

}
