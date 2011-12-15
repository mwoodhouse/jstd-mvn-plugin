package org.mw.jstd;

import com.google.jstestdriver.FailureException;
import com.google.jstestdriver.JsTestDriver;
import com.google.jstestdriver.config.CmdFlags;
import com.google.jstestdriver.config.CmdLineFlagsFactory;
import com.google.jstestdriver.config.InvalidFlagException;
import com.google.jstestdriver.config.UnreadableFilesException;
import com.google.jstestdriver.embedded.JsTestDriverBuilder;
import com.google.jstestdriver.guice.TestResultPrintingModule;
import com.google.jstestdriver.util.RetryException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.ArrayList;

/**
 * runs jstd within maven
 * @requiresDependencyResolution
 * @goal jstd
 * @phase test
 */
public class JSTDMvnPlugin extends AbstractMojo
{
    /**
     * config file path
     *
     * @parameter
     * @required
     */
    private String configFilePath;

    /**
     * test output path
     *
     * @parameter
     */
    private String testOutputPath;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        printBanner();

        run(getArgs());
    }

    private void run(String[] args) throws MojoExecutionException, MojoFailureException
    {
        final MvnTestResultLogger mvnTestResultLogger = new MvnTestResultLogger(getLog());

        try
        {
            CmdFlags cmdLineFlags = new CmdLineFlagsFactory().create(args);

            // todo - look at how plugins can fit into the build process 
//            List<Plugin> cmdLinePlugins = cmdLineFlags.getPlugins();
//            final PluginLoader pluginLoader = new PluginLoader();
//            final List<Module> pluginModules = pluginLoader.load(cmdLinePlugins);
//            getLog().info(String.format("loaded plugins %s", pluginModules));

            JsTestDriverBuilder builder = new JsTestDriverBuilder();
            builder.setBaseDir(cmdLineFlags.getBasePath().getCanonicalFile());
            builder.setConfigurationSource(cmdLineFlags.getConfigurationSource());
//            builder.addPluginModules(pluginModules);
            builder.withPluginInitializer(TestResultPrintingModule.TestResultPrintingInitializer.class);
            builder.setRunnerMode(cmdLineFlags.getRunnerMode());
            builder.setFlags(cmdLineFlags.getUnusedFlagsAsArgs());
            builder.addTestListener(mvnTestResultLogger);
            JsTestDriver jstd = builder.build();

            jstd.runConfiguration();
        }
        catch (InvalidFlagException e)
        {
            throw new MojoExecutionException("Invalid Flags :", e);
        }
        catch (UnreadableFilesException e)
        {
            throw new MojoExecutionException("Configuration Error: \n" + e.getMessage(), e);
        }
        catch (RetryException e)
        {
            throw new MojoFailureException("Tests failed due to unexpected environment issue: " + e.getCause().getMessage());
        }
        catch (FailureException e)
        {
            throw new MojoFailureException(e.getMessage());
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Unexpected plugin error: " + e.getMessage(), e);
        }

        System.out.println("\n");
        System.out.println(" J S T D  MVN Plugin - Complete... :)");
        System.out.println("\n");
    }

    private String[] getArgs()
    {
        final ArrayList<String> args = new ArrayList<String>();

        args.add("--tests");
        args.add("all");
        args.add("--config");
        args.add(configFilePath);

        if(testOutputPath != null)
        {
            args.add("--testOutput");
            args.add(testOutputPath);
        }
        return args.toArray(new String[]{});
    }

    private void printBanner() {
        getLog().info("\n");
        getLog().info("-------------------------------------------");
        getLog().info(" J S T D  MVN PLUGIN ");
        getLog().info("-------------------------------------------");
    }

    public void setConfigFilePath(final String configFilePath)
    {
        this.configFilePath = configFilePath;
    }

    public void setTestOutputPath(final String testOutputPath)
    {
        this.testOutputPath = testOutputPath;
    }
}