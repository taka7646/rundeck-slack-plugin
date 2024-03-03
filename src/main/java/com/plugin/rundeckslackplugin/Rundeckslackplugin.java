package com.plugin.rundeckslackplugin;

import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.PropertyScope;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.descriptions.Password;
import com.dtolabs.rundeck.plugins.descriptions.TextArea;
import java.util.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import com.google.gson.Gson;

@Plugin(service="Notification",name="rundeck-slack-plugin")
@PluginDescription(title="rundeck-slack-plugin", description="Notification plugin for slack.")
public class Rundeckslackplugin implements NotificationPlugin{

    @PluginProperty(name = "baseUrl",title = "Base URL",description = "Slack Incoming WebHook Base URL", defaultValue = "https://hooks.slack.com/services", scope=PropertyScope.Instance)
    private String baseUrl;
    @PluginProperty(name = "webHookToken", title = "Webhookトークン", description = "Incoming Webhookトークン T????/B?????/*****", defaultValue = "", scope=PropertyScope.Instance)
    @Password
    private String webHookToken;
    @PluginProperty(name = "channel", title = "チャンネル", description = "Slack チャンネル", defaultValue = "", scope=PropertyScope.Instance)
    private String channel;
    @PluginProperty(name = "message", title = "送信メッセージ", description = "送信メッセージ(mrkdwn)", defaultValue = "", scope=PropertyScope.Instance)
    @TextArea
    private String message;
    @PluginProperty(name = "messageFile", title = "送信メッセージファイル", description = "送信するメッセージのファイル名を指定してください", defaultValue = "", scope=PropertyScope.Instance)
    private String messageFile;

    public boolean postNotification(String trigger, Map executionData, Map config) {
        if ("".equals(this.message) || this.message == null) {
            return true;
        }
        // Gson gson = new Gson();
        // System.err.println("config=" + gson.toJson(config) + ", exec=" + gson.toJson(executionData));
        this.send(this.createMessage(executionData, this.message));
        return true;
    }

    public String createMessage(Map executionData, String message)
    {
        // メッセージを置換
        message = message.replace("\\n", "\n");
        message = message.replace("<br>", "\n");
        message = message.replace("{STATUS}", (String)executionData.get("status"));
        message = message.replace("{JOB}", (String)executionData.get("project"));
        message = message.replace("{URL}", (String)executionData.get("href"));
        message = message.replace("{ID}", executionData.get("id").toString());
        message = message.replace("{USER}", (String)executionData.get("user"));
        message = message.replace("{NODE}", (String)executionData.get("succeededNodeListString"));
        Map<String,String> job = (Map<String,String>)executionData.get("job");
        message = message.replace("{JOB_URL}", job.get("href"));
        // JOBオプションを置換
        Map<String,Object> context = (Map<String,Object>)executionData.get("context");
        Map<String,String> option = (Map<String,String>)context.get("option");
        for (Map.Entry<String,String> e : option.entrySet()) {
            message = message.replace("{OPT:"+e.getKey()+"}", e.getValue());
        }
        // メッセージファイルを追加
        if (messageFile != null && !"".equals(messageFile)) {
            File f = new File(messageFile);
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String text;
                StringBuilder sb = new StringBuilder();
                sb.append(message);
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                while ((text = br.readLine()) != null) {
                    sb.append(text).append("\n");
                }
                message = sb.toString();
            } catch (Exception e) {
                System.err.println("load file error:" + messageFile + "\n" + e.getMessage());
            }
        }

        return message;
    }

    public String send(String message)
    {
        try {
            URL url = new URL(this.baseUrl + "/" + this.webHookToken);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            
            con.setRequestMethod("POST");
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoInput(true);
            con.setDoOutput(true);
            SlackMessage sm = new SlackMessage();
            sm.mrkdwn = true;
            sm.text = message;
            sm.channel = this.channel;
            Gson gson = new Gson();
            String payload = gson.toJson(sm);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            wr.write(payload);
            wr.flush();
            wr.close();

            InputStream input = con.getInputStream();
            String res = new Scanner(input,"UTF-8").useDelimiter("\\A").next();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}