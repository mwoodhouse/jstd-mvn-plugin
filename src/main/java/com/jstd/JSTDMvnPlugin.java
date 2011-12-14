package com.jstd;

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

import java.util.logging.LogManager;

/**
 * runs jstd within maven
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

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        printBanner();

        run(new String[] {"--tests", "all", "--config", configFilePath});
    }

    private void run(String[] args) throws MojoExecutionException, MojoFailureException
    {
        final MvnTestResultLogger mvnTestResultLogger = new MvnTestResultLogger(getLog());

        try
        {
            CmdFlags cmdLineFlags = new CmdLineFlagsFactory().create(args);

            LogManager.getLogManager().readConfiguration(cmdLineFlags.getRunnerMode().getLogConfig());

            JsTestDriverBuilder builder = new JsTestDriverBuilder();
            builder.setBaseDir(cmdLineFlags.getBasePath().getCanonicalFile());
            builder.setConfigurationSource(cmdLineFlags.getConfigurationSource());
            builder.withPluginInitializer(TestResultPrintingModule.TestResultPrintingInitializer.class);
            builder.setRunnerMode(cmdLineFlags.getRunnerMode());
            builder.setFlags(cmdLineFlags.getUnusedFlagsAsArgs());
            builder.addTestListener(mvnTestResultLogger);
            JsTestDriver jstd = builder.build();

            jstd.runConfiguration();

            getLog().info("Finished action run.");
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
//            System.out.println("Unexpected Runner Condition: " + e.getMessage() + "\n Use --runnerMode DEBUG for more information.");
        }
    }

    public void setConfigFilePath(final String configFilePath)
    {
        this.configFilePath = configFilePath;
    }

    private void printBanner() {
        System.out.println("\n" +
                "-------------------------------------------\n" +
                " J S T D  MVN PLUGIN                       \n" +
                "-------------------------------------------\n");
    }
}