package ban.johnson.tank.command;

public class LizardCommand implements Command {

    @Override
    public String render() {
        return String.format("USE_LIZARD");
    }
}
