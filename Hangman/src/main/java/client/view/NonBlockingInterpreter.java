package client.view;

import client.controller.ClientController;
import client.network.OutputHandler;
import common.*;

import java.util.Scanner;


public class NonBlockingInterpreter implements Runnable{
    private boolean receivingCmds = false;
    private ClientController contr;
    private final Scanner console = new Scanner(System.in);

    private final OutputManager output = new OutputManager();

    public void start(){
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        contr = new ClientController();
        new Thread(this).start();

    }
    public void run() {
        while (receivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case QUIT:
                        receivingCmds = false;
                        contr.disconnect();
                        break;
                    case START:
                        contr.startNewGame();
                        break;
                    case GUESS:
                        System.out.println("You guessed: " + cmdLine.getArgument(0));
                        contr.guess(cmdLine.getArgument(0));
                        break;
                    case CONNECT:
                        contr.connect("127.0.0.1",Definitions.PORT, new ConsoleOutput());
                        break;
                    case NO_COMMAND:
                        break;
                    default: break;

                }
            } catch (Exception e) {
                //outMgr.println("Operation failed");
            }
        }
    }


    private String readNextLine() {
        output.print(Definitions.PROMPT);
        return console.nextLine();
    }


    private class ConsoleOutput implements OutputHandler {

        public void handleMsg(String msg) {
            output.println((String) msg);
            //outputter.print(Definitions.PROMPT);
        }
    }
}
