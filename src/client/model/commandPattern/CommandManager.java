package client.model.commandPattern;

import client.model.commandPattern.commands.*;

import java.util.HashMap;

public class CommandManager {
    private static CommandManager commandManager;
    private static final HashMap<String, Class<? extends ICommand>> COMMANDS = new HashMap<>();

    private CommandManager() {
        this.registerCommand(ConnectCommand.COMMAND_NAME, ConnectCommand.class);
        this.registerCommand(ChatCommand.COMMAND_NAME, ChatCommand.class);
    }

    public void setLeftCommands(int level) {
        if (level == 1) {
            this.registerCommand(LuchadorCommand.COMMAND_NAME, LuchadorCommand.class);
            this.registerCommand(InitCommand.COMMAND_NAME, InitCommand.class);
        }
        if (level == 2) {
            this.registerCommand(AttackCommand.COMMAND_NAME, AttackCommand.class);
            this.registerCommand(SkipCommand.COMMAND_NAME, SkipCommand.class);
            this.registerCommand(GiveUpCommand.COMMAND_NAME, GiveUpCommand.class);
            this.registerCommand(ShowUnavailableCommand.COMMAND_NAME, ShowUnavailableCommand.class);
            this.registerCommand(ShowPercentageCommand.COMMAND_NAME, ShowPercentageCommand.class);
            this.registerCommand(ShowAliveCommand.COMMAND_NAME, ShowAliveCommand.class);
            this.registerCommand(ConsultCommand.COMMAND_NAME, ConsultCommand.class);
            this.registerCommand(PowerCommand.COMMAND_NAME, PowerCommand.class);
            this.registerCommand(LogCommand.COMMAND_NAME, LogCommand.class);
            this.registerCommand(NumCommand.COMMAND_NAME, NumCommand.class);
        }
    }

    public void removeLevelTwoCommands() {
        COMMANDS.remove(AttackCommand.COMMAND_NAME.toUpperCase());
        COMMANDS.remove(SkipCommand.COMMAND_NAME.toUpperCase());
        COMMANDS.remove(GiveUpCommand.COMMAND_NAME.toUpperCase());
    }

    public static synchronized CommandManager getInstance() {
        if (commandManager == null) {
            commandManager = new CommandManager();
        }
        return commandManager;
    }

    public ICommand getCommand(String commandName) {
        if (COMMANDS.containsKey(commandName.toUpperCase())) {
            try {
                return COMMANDS.get(commandName.toUpperCase()).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorCommand();
            }
        } else {
            return new NotFoundCommand();
        }
    }

    private void registerCommand(String commandName, Class<? extends ICommand> command) {
        COMMANDS.put(commandName.toUpperCase(), command);
    }
}

