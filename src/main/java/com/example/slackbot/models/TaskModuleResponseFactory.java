package com.example.slackbot.models;

import com.microsoft.bot.schema.teams.TaskModuleContinueResponse;
import com.microsoft.bot.schema.teams.TaskModuleMessageResponse;
import com.microsoft.bot.schema.teams.TaskModuleResponse;
import com.microsoft.bot.schema.teams.TaskModuleTaskInfo;

public class TaskModuleResponseFactory {
    public static TaskModuleResponse createResponse(String message) {
        return new TaskModuleResponse() {{
            setTask(new TaskModuleMessageResponse() {{
                setValue(message);
            }});
        }};
    }

    public static TaskModuleResponse createResponse(TaskModuleTaskInfo taskInfo) {
        return new TaskModuleResponse() {{
            setTask(new TaskModuleContinueResponse() {{
                setValue(taskInfo);
            }});
        }};
    }

    public static TaskModuleResponse toTaskModuleResponse(TaskModuleTaskInfo taskInfo) {
        return createResponse(taskInfo);
    }
}
