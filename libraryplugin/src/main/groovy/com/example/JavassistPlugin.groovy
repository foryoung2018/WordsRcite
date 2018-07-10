package com.example

import org.gradle.api.Plugin
import org.gradle.api.Project

public class JavassistPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.dependencies {
            compile 'org.aspectj:aspectjrt:1.8.9'
        }
        def log = project.logger
        log.error "========================";
        log.error "Javassist开始修改Class!";
        log.error "========================";
        project.android.registerTransform(new JavassistTransform(project))
    }
}