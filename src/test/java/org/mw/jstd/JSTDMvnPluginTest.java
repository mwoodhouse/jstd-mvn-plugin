package org.mw.jstd;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

public class JSTDMvnPluginTest
{
    @Test
    public void shouldRunTests() throws MojoExecutionException, MojoFailureException
    {
        JSTDMvnPlugin jstdMvnPlugin = new JSTDMvnPlugin();

        jstdMvnPlugin.setConfigFilePath("runner.conf");
        jstdMvnPlugin.setTestOutputPath("coverage");

        jstdMvnPlugin.execute();
    }

}
