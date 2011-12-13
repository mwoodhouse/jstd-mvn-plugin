import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

public class JSTDMvnPlugin extends AbstractMojo
{
    public void execute() throws MojoExecutionException
    {
        getLog().info("JSTDMvnPlugin, execute.");
    }
}