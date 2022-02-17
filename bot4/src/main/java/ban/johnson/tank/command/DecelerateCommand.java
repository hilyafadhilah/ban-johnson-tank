package ban.johnson.tank.command;

public class DecelerateCommand implements Command {

    @Override
    public String render() {
        return String.format("DECELERATE");
    }
}
