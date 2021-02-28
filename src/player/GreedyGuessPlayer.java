package player;

import java.util.*;
import world.World;

/**
 * Greedy guess player (task B).
 */
public class GreedyGuessPlayer extends AbstractPlayer {
    boolean targetingMode;

    public List<Coordinate> targetingCdn;
    public List<Coordinate> huntingCdn;
    public List<Coordinate> guessCdnHist;


    public void initialisePlayer(World world) {
        this.world = world;
        this.foeShotHist = new ArrayList<Coordinate>();

        targetingMode = false;

        initialiseGuesses();
        initialiseHitsReceived();
    } // end of initialisePlayer()


    private void initialiseGuesses() {
        targetingCdn = new ArrayList<Coordinate>();
        huntingCdn = new ArrayList<Coordinate>();
        guessCdnHist = new ArrayList<Coordinate>();
        // Generate a guess for every coordinate in the world
        // and then shuffle the list
        for(int i=0; i<world.numRow; i++) {
            // Checker board pattern
            for(int j=0; j<world.numColumn; j+=2) {
                if(i%2!=0 && j==0) j++;
                huntingCdn.add(new Coordinate(i, j));
            }
        }
        Collections.shuffle(huntingCdn);
    } // end of initialiseGuesses()


    public Guess makeGuess() {
        Coordinate cdn;
        
        if(targetingMode) {
            cdn = targetingCdn.remove(targetingCdn.size()-1);
        } else {
            cdn = huntingCdn.remove(huntingCdn.size()-1);
        }
        
        guessCdnHist.add(cdn);
        return cdn.toGuess();
    } // end of makeGuess()


    public void update(Guess guess, Answer answer) {
        // If is hit, then enter targeting mode 
        // fire around the hit
        if(answer.isHit) {
            targetingMode = true;
            addTargets(guess);
        }
        
        // To exit targeting mode when addTargets() can't find
        // any new targets
        if(targetingMode && !(targetingCdn.size()>0)) {
            targetingMode = false;
        }
    } // end of update()


    public void addTargets(Guess guess) {
        Coordinate cdn = new Coordinate(guess);

        int[][] neighbours = new int[][] {
            {0,-1}, // Up
            {0,1}, // Down
            {-1,0}, // Left
            {1,0}, // Right
        };
        
        // Add the neighbours of the guess to the targeting list
        Coordinate neighbour;
        for(int i=0; i<neighbours.length; i++) {
            neighbour = new Coordinate(cdn.row+neighbours[i][1], cdn.column+neighbours[i][0]);
            // Check that neighbour is inside the map,
            // hasn't been guessed before and
            // isn't already in targeting list
            if(neighbour.column>=0 && neighbour.column< world.numColumn 
                    && neighbour.row>=0 && neighbour.row< world.numRow
                    && !guessCdnHist.contains(neighbour)
                    && !targetingCdn.contains(neighbour)) {
                huntingCdn.remove(neighbour);
                targetingCdn.add(neighbour);
            }
        }
    } // end of addTargets()

} // end of class GreedyGuessPlayer
