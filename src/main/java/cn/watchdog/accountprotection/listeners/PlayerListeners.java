package cn.watchdog.accountprotection.listeners;

import cn.watchdog.accountprotection.*;
import cn.watchdog.accountprotection.utils.*;
import cn.watchdog.accountprotection.utils.storage.implementation.sql.*;
import cn.watchdog.accountprotection.utils.storage.misc.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.*;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.event.*;

import java.sql.*;
import java.util.*;

public class PlayerListeners implements Listener {
    public static HashMap<UUID, Boolean> pass = new HashMap<>();

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();
        UUID u = p.getUniqueId();
        if (p.hasPermission("kinomc.staff")) {
            try {
                AccountProtection accountProtection = AccountProtection.getInstance();
                SqlStorage sqlStorage = AccountProtection.getSqlStorage();
                PLAYER_ACCOUNT_INFO player_account_info = sqlStorage.selectPlayerAccountInfo(u);
                boolean empty = player_account_info.isEmpty();
                player_account_info.init(); //初始化
                pass.put(u, false);
                if (empty || !player_account_info.isBind()) {
                    // 未绑定
                    TextComponent message = new TextComponent(ChatColor.RED + "服务器启用2FA，您未设置，点击此消息扫描二维码进行设置。");
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://cli.im/api/qrcode/code?text=" + GoogleAuthenticator.getQRBarcode(p.getName(), player_account_info.getSecret_key())));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("点击此消息扫描二维码进行设置").create()));
                    p.sendMessage(message);
                    p.sendMessage(new TextComponent(ChatColor.GRAY + "或者您可以手动输入：" + player_account_info.getSecret_key()));
                    p.sendMessage(new TextComponent(ChatColor.GOLD + "设置完成后，在聊天栏处输入验证码以进入服务器！"));
                } else {
                    //已绑定
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        UUID u = p.getUniqueId();
        if (p.hasPermission("2fa.staff")) {
            if (!pass.get(u)) {
                e.setCancelled(true);
                try {
                    SqlStorage sqlStorage = AccountProtection.getSqlStorage();
                    PLAYER_ACCOUNT_INFO player_account_info = sqlStorage.selectPlayerAccountInfo(u);
                    long code;
                    try {
                        code = Long.parseLong(e.getMessage());
                    } catch (Throwable ea) {
                        p.sendMessage(new TextComponent(ChatColor.RED + "请输入正确的2FA验证码！"));
                        return;
                    }
                    long t = System.currentTimeMillis();
                    GoogleAuthenticator ga = new GoogleAuthenticator();
                    ga.setWindowSize(5);
                    boolean r = ga.check_code(player_account_info.getSecret_key(), code, t);
                    if (r) {
                        p.sendMessage(new TextComponent(ChatColor.GREEN + "验证成功！"));
                        player_account_info.setBind(true);
                        pass.put(u, true);
                    } else {
                        p.sendMessage(new TextComponent(ChatColor.RED + "请输入正确的2FA验证码！"));
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }
}
