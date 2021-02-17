package com.example.slackbot.models;

public class TaskModuleUIConstants {
    private TaskModuleUIConstants() {

    }

    public static final UISettings YOUTUBE = new UISettings(
            1000,
            700,
            "YouTube Video",
            TaskModuleIds.YOUTUBE,
            "YouTube"
    );

    public static final UISettings CUSTOMFORM = new UISettings(
            510,
            450,
            "Custom Form",
            TaskModuleIds.CUSTOMFORM,
            "Custom Form"
    );

    public static final UISettings ADAPTIVECARD = new UISettings(
            400,
            200,
            "Adaptive Card: Inputs",
            TaskModuleIds.ADAPTIVECARD,
            "Adaptive Card"
    );
}
