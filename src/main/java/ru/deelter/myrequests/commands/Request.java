package ru.deelter.myrequests.commands;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.deelter.myrequests.Config;
import ru.deelter.myrequests.utils.MyRequest;
import ru.deelter.myrequests.utils.Other;

public class Request implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args[0] == null) {
            sender.sendMessage(Other.color("&6# &fUsage: " + label + " &a[send|reload|get>] <id>"));
            return true;
        }

        if (args[1] != null) {
            MyRequest myRequest = MyRequest.getRequest(args[1]);
            if (myRequest == null) {
                sender.sendMessage(Config.MSG_INVALID_ID);
                return true;
            }

            /* Send request command */
            if (args[0].equalsIgnoreCase("send")) {
                myRequest.send();

                String response = myRequest.getResponse();
                if (sender instanceof Player) {
                    sendRequestMessage((Player) sender, args[1], myRequest.getResponse(), myRequest.getResponseCode());
                    return true;
                }
                Other.log(Config.MSG_SENDING_REQUEST.replace("ID", args[1]).replace("RESPONSE", response));
            }

            /* Getting response */
            else if (args[0].equalsIgnoreCase("get")) {
                String response = myRequest.getResponse();
                if (sender instanceof Player) {
                    sendRequestMessage((Player) sender, args[1], myRequest.getResponse(), myRequest.getResponseCode());
                    return true;
                }
                Other.log(Config.MSG_SENDING_REQUEST.replace("ID", args[1]).replace("RESPONSE", response));
            }
        }

        /* Reload configuration command */
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.isOp()) {
                sender.sendMessage(Config.MSG_NO_PERMISSION);
                return true;
            }
            Config.reload();
        }
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
}
