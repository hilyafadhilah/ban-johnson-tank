package ban.johnson.tank.command;

public class DoNothingCommand implements Command {
    @Override
    public String render() {
        return "NOTHING";
    }
}
