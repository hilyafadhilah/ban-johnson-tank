package ban.johnson.tank.command;

public class AccelerateCommand implements Command {

    @Override
    public String render() {
        return String.format("ACCELERATE");
    }

}
