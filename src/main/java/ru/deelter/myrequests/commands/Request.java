package ru.deelter.myrequests.commands;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.deelter.myrequests.Config;
import ru.deelter.myrequests.utils.MyRequest;
import ru.deelter.myrequests.utils.Other;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Other.color("&6# &fUsage: " + label + " &a[send|reload|get>] <id>"));
            return true;
        }

        /* Reload configuration command */
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.isOp()) {
                sender.sendMessage(Config.MSG_NO_PERMISSION);
                return true;
            }
            Config.reload();
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Other.color("&6# &fUsage: " + label + " &a[send|reload|get>] <id>"));
            return true;
        }

        MyRequest myRequest = MyRequest.getRequest(args[1]);
        if (myRequest == null) {
            sender.sendMessage(Config.MSG_INVALID_ID);
            return true;
        }

        myRequest = myRequest.clone();

        /* Send request command */
        if (args[0].equalsIgnoreCase("send")) {
            if (args.length == 3) {

                Map<String, String> body = new HashMap<>(myRequest.getBody());
                String[] params = args[2].split(",");
                for (String param : params) {

                    String[] entry = param.split("=");
                    if (!body.containsKey(entry[0])) {
                        sender.sendMessage(Other.color("&cПараметр &6" + entry[0] + "&c не найден"));
                        return true;
                    }

                    /* With space %20 */
                    body.replace(entry[0], entry[1].replace(Config.SPACE_SYMBOL, " "));
                }
                myRequest.setBody(body);
            }

            myRequest.send();
            if (sender instanceof Player) {
                sendRequestMessage((Player) sender, args[1], myRequest.getResponse(), myRequest.getResponseCode());
                return true;
            }
            Other.log(Config.MSG_SENDING_REQUEST.replace("%ID%", args[1]).replace("%RESPONSE%", myRequest.getResponse()));
        }

            /* Getting response
            else if (args[0].equalsIgnoreCase("get")) {
                String response = myRequest.getResponse();
                if (sender instanceof Player) {
                    sendRequestMessage((Player) sender, args[1], myRequest.getResponse(), myRequest.getResponseCode());
                    return true;
                }
                Other.log(Config.MSG_SENDING_REQUEST.replace("ID", args[1]).replace("RESPONSE", response));
            }
             */
        return true;
    }

    /* Just funny text format */
    private void sendRequestMessage(Player sender, String id, String response, int code) {
        TextComponent component = new TextComponent(Config.MSG_PLAYER_REQUEST.replace("ID", id));
        TextComponent resp = new TextComponent(Other.color("&f[&6RESPONSE&f]"));

        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Other.color("&6Response code: &f" + code + "\n&6Response: &f" + response)));
        resp.setHoverEvent(hoverEvent);
        component.addExtra(resp);

        sender.sendMessage(component);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
