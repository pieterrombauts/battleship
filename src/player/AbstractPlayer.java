package player;

import java.util.*;
import world.World;
import ship.Ship;

/**
 * Abstract class for players
 */
public abstract class AbstractPlayer implements Player{
    // nested class for keeping the coordinates of ships or shots.
    public class Coordinate {
        public int row;
        public int column;

        public Coordinate(int row, int column) {
            this.row = row;
            this.column = column;
        }

        public Coordinate(Guess guess) {
            this.row = guess.row;
            this.column = guess.column;
        }

        public Guess toGuess() {
            Guess guess = new Guess();
            guess.row = row;
            guess.column = column;
            return guess;
        }

        public World.Coordinate toWorldCoordinate() {
            World.Coordinate wCdn = world.new Coordinate();
            wCdn.row = row;
            wCdn.column = column;
            return wCdn;
        }

        @Override
        public boolean equals(Object object) {
            boolean isSame = false;
            if (object != null && object instanceof Coordinate) {
                Coordinate other = (Coordinate) object;
                isSame = (this.row == other.row && this.column == other.column);
            }

            return isSame;
        }

    }

    public World world;
    public List<Coordinate> foeShotHist;
    public Map<Ship, List<Coordinate>> hitsReceived;

    @Override
    public abstract void initialisePlayer(World world);


    public void initialiseHitsReceived() {
        // Create a map between the ship object and the list that tracks hits
        hitsReceived = new HashMap<Ship, List<Coordinate>>();
        for(World.ShipLocation sl: world.shipLocations) {
            hitsReceived.put(sl.ship, new ArrayList<Coordinate>());
        }
    }


    @Override
    public Answer getAnswer(Guess guess) {
        Answer ans = new Answer();

        Coordinate cdn = new Coordinate(guess);
        // If coordinate has been hit before, then there is no hit
        if(!foeShotHist.contains(cdn)) {
            foeShotHist.add(cdn);
            for(World.ShipLocation sl: world.shipLocations) { 
                if(sl.coordinates.contains(cdn.toWorldCoordinate())) {
                    List<Coordinate> shipHits = hitsReceived.get(sl.ship);
                    ans.isHit = true;

                    // Record the hit if it is new
                    if(!shipHits.contains(cdn)) {
                        shipHits.add(cdn);
                    }

                    // If the size of the hit list is the same as 
                    // the ship coordinates, then it has sunk
                    if(shipHits.size()==sl.coordinates.size()) {
                        ans.shipSunk = sl.ship;
                    }

                    break;
                }
            }
        }

        return ans;
    } // end of getAnswer()


    @Override
    public abstract Guess makeGuess();


    @Override
    public abstract void update(Guess guess, Answer answer);


    @Override
    public boolean noRemainingShips() {
        // If the size of the hit list is the same as
        // the ship coordinates, then it has sunk
        for(World.ShipLocation sl: world.shipLocations) {
            if(hitsReceived.get(sl.ship).size() < sl.coordinates.size()) {
                return false;
            }
        }

        return true;
    } // end of noRemainingShips()

} // end of class RandomGuessPlayer
