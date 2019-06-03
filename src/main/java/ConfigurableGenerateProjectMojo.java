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

  @Parameter
  private File propertyFile;

  @Parameter( defaultValue = "${basedir}", readonly = true )
  private File baseDir;

  @Parameter( defaultValue = "${session}", readonly = true )
  private MavenSession sessionRef;

  public void execute()
      throws MojoExecutionException, MojoFailureException {


    if (propertyFile != null && propertyFile.exists()) {
      try {

        Properties prp = new Properties();
        prp.load(new FileInputStream(propertyFile));
        sessionRef.getUserProperties().putAll(prp);


        File targetDir = new File( baseDir, prp.getProperty("artifactId"));
        if (targetDir.exists()) {
          FileUtils.deleteDirectory(targetDir);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }


    super.execute();
  }

}
