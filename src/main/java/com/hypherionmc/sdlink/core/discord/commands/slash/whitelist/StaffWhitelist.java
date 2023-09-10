package com.hypherionmc.sdlink.core.discord.commands.slash.whitelist;

import com.hypherionmc.sdlink.core.accounts.MinecraftAccount;
import com.hypherionmc.sdlink.core.discord.commands.slash.SDLinkSlashCommand;
import com.hypherionmc.sdlink.core.messaging.Result;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class StaffWhitelist extends SDLinkSlashCommand {

    public StaffWhitelist() {
        super(true);
        this.name = "staffwhitelist";
        this.help = "Allow staff members to whitelist other minecraft players, without verification";

        List<OptionData> options = new ArrayList<>() {{
           add(new OptionData(OptionType.USER, "discorduser", "The discord user the minecraft account belongs to").setRequired(true));
           add(new OptionData(OptionType.STRING, "mcname", "The minecraft account to link to the user").setRequired(true));
        }};

        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        String mcname = event.getOption("mcname").getAsString();
        User user = event.getOption("discorduser").getAsUser();

        Member member = event.getGuild().getMemberById(user.getId());

        if (member == null) {
            event.reply(user.getEffectiveName() + " is not a member of this discord server").setEphemeral(true).queue();
            return;
        }

        MinecraftAccount minecraftAccount = MinecraftAccount.standard(mcname);
        Result result = minecraftAccount.whitelistAccount(member, event.getGuild());
        event.reply(result.getMessage()).setEphemeral(true).queue();
    }

}
