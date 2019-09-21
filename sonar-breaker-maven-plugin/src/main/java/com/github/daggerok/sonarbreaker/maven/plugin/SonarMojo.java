package com.github.daggerok.sonarbreaker.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
// import org.apache.maven.plugins.annotations.*;
//
// @Mojo
public class SonarMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("hello!");
    }
}
