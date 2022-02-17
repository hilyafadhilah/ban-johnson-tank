package ban.johnson.tank.command;

public class FixCommand implements Command {

    @Override
    public String render() {
        return String.format("FIX");
    }

}
