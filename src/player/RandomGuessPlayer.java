package player;

import java.util.*;
import world.World;

/**
 * Random guess player (task A).
 */
public class RandomGuessPlayer extends AbstractPlayer {
    public List<Coordinate> guesses;


    public void initialisePlayer(World world) {
        this.world = world;
        this.foeShotHist = new ArrayList<Coordinate>();

        initialiseGuesses();
        initialiseHitsReceived();
    } // end of initialisePlayer()


    private void initialiseGuesses() {
        // Generate a guess for every coordinate in the world
        // and then shuffle the list
        guesses = new ArrayList<Coordinate>();
        for(int i=0; i<world.numRow; i++) {
            for(int j=0; j<world.numColumn; j++) {
                guesses.add(new Coordinate(i, j));
            }
        }
        Collections.shuffle(guesses);
    } // end of initialiseGuesses()


    public Guess makeGuess() {
        // Take from the end of the list
        return guesses.remove(guesses.size()-1).toGuess();
    } // end of makeGuess()


    public void update(Guess guess, Answer answer) {
        // Not needed for this strategy 
    } // end of update()

} // end of class RandomGuessPlayer
