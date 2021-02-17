package com.example.slackbot.bot;

import com.example.slackbot.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsActivityHandler;
import com.microsoft.bot.integration.Async;
import com.microsoft.bot.integration.Configuration;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.CardAction;
import com.microsoft.bot.schema.HeroCard;
import com.microsoft.bot.schema.Serialization;
import com.microsoft.bot.schema.teams.TaskModuleAction;
import com.microsoft.bot.schema.teams.TaskModuleRequest;
import com.microsoft.bot.schema.teams.TaskModuleResponse;
import com.microsoft.bot.schema.teams.TaskModuleTaskInfo;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class TeamsTaskModuleBot extends TeamsActivityHandler {
    private final String baseUrl;
    private final List<UISettings> actions = Arrays.asList(
            TaskModuleUIConstants.ADAPTIVECARD,
            TaskModuleUIConstants.CUSTOMFORM,
            TaskModuleUIConstants.YOUTUBE
    );

    public TeamsTaskModuleBot(Configuration configuration) {
        baseUrl = configuration.getProperty("BaseUrl");
    }

    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
        return turnContext.sendActivity(MessageFactory.attachment(Arrays.asList(
                getTaskModuleHeroCardOptions(),
                getTaskModuleAdaptiveCardOptions()
                ))).thenApply(resourceResponse -> null);
    }

    @Override
    protected CompletableFuture<TaskModuleResponse> onTeamsTaskModuleFetch(TurnContext turnContext, TaskModuleRequest taskModuleRequest) {
        return Async.tryCompletion(() -> {
            CardTaskFetchValue<String> value = Serialization.safeGetAs(taskModuleRequest.getData(), CardTaskFetchValue.class);

            TaskModuleTaskInfo taskInfo = new TaskModuleTaskInfo();
            switch (value.getData()) {
                case TaskModuleIds.YOUTUBE: {
                    String url = baseUrl + "/" + TaskModuleIds.YOUTUBE + ".html";
                    taskInfo.setUrl(url);
                    taskInfo.setFallbackUrl(url);
                    setTaskInfo(taskInfo, TaskModuleUIConstants.YOUTUBE);
                    break;
                }

                case TaskModuleIds.CUSTOMFORM: {
                    String url = baseUrl + "/" + TaskModuleIds.CUSTOMFORM + ".html";
                    taskInfo.setUrl(url);
                    taskInfo.setFallbackUrl(url);
                    setTaskInfo(taskInfo, TaskModuleUIConstants.CUSTOMFORM);
                    break;
                }

                case TaskModuleIds.ADAPTIVECARD:
                    taskInfo.setCard(createAdaptiveCardAttachment());
                    setTaskInfo(taskInfo, TaskModuleUIConstants.ADAPTIVECARD);
                    break;

                default:
                    break;
            }

            return taskInfo;
        }).thenApply(TaskModuleResponseFactory::toTaskModuleResponse);
    }

    @Override
    protected CompletableFuture<TaskModuleResponse> onTeamsTaskModuleSubmit(TurnContext turnContext, TaskModuleRequest taskModuleRequest) {
        return Async.tryCompletion(() ->
                MessageFactory.text("onTeamsTaskModuleSubmit TaskModuleRequest: " + new ObjectMapper().writeValueAsString(taskModuleRequest)))
                .thenCompose(turnContext::sendActivity)
                .thenApply(resourceResponse -> TaskModuleResponseFactory.createResponse("Thanks!"));
    }

    private void setTaskInfo(TaskModuleTaskInfo taskInfo, UISettings uiSettings) {
        taskInfo.setHeight(uiSettings.getHeight());
        taskInfo.setWidth(uiSettings.getWidth());
        taskInfo.setTitle(uiSettings.getTitle());
    }

    private Attachment getTaskModuleHeroCardOptions() {
        List<CardAction> buttons = actions.stream().map(cardType ->
                new TaskModuleAction(cardType.getButtonTitle(), new CardTaskFetchValue<String>() {{ setData(cardType.getId()); }})
        ).collect(Collectors.toCollection(ArrayList::new));

        HeroCard card = new HeroCard() {{
            setTitle("Task Module Invocation from Hero Card");
            setButtons(buttons);
        }};
        return card.toAttachment();
    }

    private Attachment getTaskModuleAdaptiveCardOptions() {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("adaptiveTemplate.json")
        ) {
            String cardTemplate = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            List<Map<String,Object>> cardActions = actions.stream().map(cardType -> {
                Map<String, Object> a = new HashMap<>();
                a.put("type", "Action.Submit");
                a.put("title", cardType.getButtonTitle());
                a.put("data", new AdaptiveCardTaskFetchValue<String>() {{
                    setData(cardType.getId());
                }});
                return a;
            }).collect(Collectors.toCollection(ArrayList::new));

            String adaptiveCardJson = String.format(cardTemplate, Serialization.toString(cardActions));

            return adaptiveCardAttachmentFromJson(adaptiveCardJson);
        } catch (Throwable t) {
            throw new CompletionException(t);
        }
    }

    private Attachment createAdaptiveCardAttachment() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("adaptivecard.json")) {
            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return adaptiveCardAttachmentFromJson(result);
        } catch (Throwable t) {
            throw new CompletionException(t);
        }
    }

    private Attachment adaptiveCardAttachmentFromJson(String json) throws IOException {
        return new Attachment() {{
            setContentType("application/vnd.microsoft.card.adaptive");
            setContent(new ObjectMapper().readValue(json, ObjectNode.class));
        }};
    }
}
