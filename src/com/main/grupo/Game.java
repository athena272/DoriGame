package com.main.grupo;

import com.command.grupo.Command;
import com.command.grupo.CommandWord;
import com.command.grupo.Parser;
import com.room.grupo.Room;

import java.awt.desktop.QuitEvent;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class Game 
{
    private Parser parser;
    private Room currentRoom;

    private Boolean hasDori;
    private Boolean hasTartarugas;
    private Boolean hasBaleia;

    private Boolean isDead;

    private Boolean isFinished;
        
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        Room anemona, arraia, dori, quitRoom, tubaroes, canion, tartarugas, aguasVivas, baleia, sidney, nemo, campoMinado;
      
        // create the rooms
        anemona = new Room("Anemona","Você está no começo do jogo");
        arraia = new Room("Arraia","Você está na arraia");
        quitRoom = new Room("Saida","Você está saindo do jogo");
        dori = new Room("Dori","Você encontrou a Dori");
        tubaroes = new Room("Tubaroes","Você encontrou tubarôes, fuja!");
        canion = new Room("Canion","Você chegou aos canions");
        campoMinado = new Room("CampoMinado", "Você foi papocado por minas, burrão...");
        tartarugas = new Room("Tartarugas","Você encontrou as tartarugas");
        aguasVivas = new Room("AguasVivas","Você chegou até as aguas vivas, tenha cuidado");
        baleia = new Room("Baleia","Você foi engolido por uma baleia e não tem escolha, siga o caminho que ela te levar");
        sidney = new Room("Sidney","Você chegou em Sidney");
        nemo = new Room("Nemo","Nemo deve está por perto...");
        
        // initialise room exits
        anemona.setExit("north", arraia);
        anemona.setExit("south", quitRoom);

        arraia.setExit("north", tubaroes);
        arraia.setExit("west", dori);
        arraia.setExit("south", anemona);

        dori.setExit("east", arraia);
        dori.setExit("north", tubaroes);

        tubaroes.setExit("south", arraia);
        tubaroes.setExit("north", canion);
        tubaroes.setExit("east", campoMinado);

        canion.setExit("north", aguasVivas);
        canion.setExit("west", tartarugas);
        canion.setExit("east", sidney);

        tartarugas.setExit("north", baleia);
        tartarugas.setExit("south", canion);
        tartarugas.setExit("east", aguasVivas);

        baleia.setExit("east", sidney);

        sidney.setExit("east", nemo);

        currentRoom = anemona;  // start game anemona
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
        do {
            Command command = parser.getCommand();
            isDead = isFinished =  processCommand(command);
            isFinished = currentRoom.getName().equals("Saida") ? true : false;

            if(currentRoom.getName().equals("Dori")){
                hasDori = true;
            }else if (currentRoom.getName().equals("Tubarao")){
                if(hasDori){
                    continue;
                }else{
                    isDead = true;
                    System.out.println("Voce ta sem a dori burrão...");
                }
            }else if(currentRoom.getName().equals("Tartaruga")){
                hasTartarugas = true;
            }else if(currentRoom.getName().equals("Baleia")){
                hasBaleia = true;
            }else if(currentRoom.getName().equals("Nemo")){
                if(hasDori && hasTartarugas && hasBaleia){
                    isFinished = true;
                }
            }

        } while (!isDead && !isFinished);
        if(isDead){
            System.out.println("Wasted!");
        }
        if(isFinished){
            System.out.println("Thank you for playing.  Good bye.");
        }
        

    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("Type '" + CommandWord.HELP + "' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command)
    {
        boolean wantToQuit = false;

        CommandWord commandWord = command.getCommandWord();

        switch (commandWord) {
            case UNKNOWN:
                System.out.println("I don't know what you mean...");
                break;

            case HELP:
                printHelp();
                break;

            case GO:
                goRoom(command);
                break;

            case QUIT:
                wantToQuit = quit(command);
                break;
        }
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println(currentRoom.getLongDescription());
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to go in one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command)
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
        }

    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command)
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
}
