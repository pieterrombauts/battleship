package player;

import java.util.*;
import java.util.Map.Entry;

import world.World;

/**
 * Probabilistic guess player (task C).
 * Please implement this class.
 *
 * @author Youhan Xia, Jeffrey Chan
 */
public class ProbabilisticGuessPlayer extends AbstractPlayer{
	boolean targetingMode;
	
	public List<Coordinate> huntingCdn;
	public List<Coordinate> targetingCdn;
	public List<Coordinate> guessCdnHist;
	public List<Coordinate> missedCdnHist;
	public int[][] possibilities;
	public int[] oneWideShips;
	public int[] twoWideShips;
	
    public void initialisePlayer(World world) {
        this.world = world;
        this.foeShotHist = new ArrayList<Coordinate>();
        
        targetingMode = false;
        
        initialiseHitsReceived();
        initialiseGuesses();
    } // end of initialisePlayer()
    
    public void initialiseGuesses() {
    	huntingCdn = new ArrayList<Coordinate>();
    	targetingCdn = new ArrayList<Coordinate>();
    	guessCdnHist = new ArrayList<Coordinate>();
    	missedCdnHist = new ArrayList<Coordinate>();
    	possibilities = new int[world.numRow][world.numColumn];
    	oneWideShips = new int[] {4, 3, 2};
    	twoWideShips = new int[] {3, 2};
    	
    	updatePoss(null, null);
    }
    
    public Coordinate getHighestPoss(List<Coordinate> cdnList) {
    	List<Coordinate> highPoss = new ArrayList<Coordinate>();
    	Coordinate highPossCdn;
    	int max = 0;
    	
    	for (Coordinate cdn: cdnList) {
			if (lookupPoss(cdn) > max) {
				max = lookupPoss(cdn);
				highPoss.clear();
				highPoss.add(cdn);
			} else if (lookupPoss(cdn) == max) {
				highPoss.add(cdn);
			}
		}
		
		Collections.shuffle(highPoss);
		highPossCdn = highPoss.remove(highPoss.size() - 1);
		//highPossCdn = highPoss.remove(0);
		cdnList.remove(highPossCdn);
    	
		return highPossCdn;
    }
    
    public void updatePoss(Guess guess, Answer answer) {
    	Coordinate cdn;
    	
    	if (guess == null) {
    		for (int i = 0; i < world.numRow; i++) {
        		for (int j = 0; j < world.numColumn; j++) {
        			cdn = new Coordinate(i, j);
        			huntingCdn.add(cdn);
        			possibilities[cdn.row][cdn.column] = calculatePossibilities(cdn);
        		}
        	}
    	} else {
    		if (answer.shipSunk != null) {
    			for (int i = 0; i < world.numRow; i++) {
            		for (int j = 0; j < world.numColumn; j++) {
            			cdn = new Coordinate(i, j);
            			if (cdn.column>=0 && cdn.column< world.numColumn 
                        		&& cdn.row>=0 && cdn.row< world.numRow
                        		&& !guessCdnHist.contains(cdn)) {
    	        			possibilities[cdn.row][cdn.column] = calculatePossibilities(cdn);
            			}
            		}
            	}
    		} else {
    			cdn = new Coordinate(guess);
        		Coordinate updateCdn;
        		
        		for (int i = -3; i <= 3; i++) {
            		for (int j = -3; j <= 3; j++) {
            			updateCdn = new Coordinate(cdn.row + i, cdn.column + j);
            			if (updateCdn.column>=0 && updateCdn.column< world.numColumn 
                        		&& updateCdn.row>=0 && updateCdn.row< world.numRow
                        		&& !guessCdnHist.contains(updateCdn)) {
    	        			possibilities[updateCdn.row][updateCdn.column] = calculatePossibilities(updateCdn);
            			}
            		}
            	}
    		}
    	}
    }
    
    public int calculatePossibilities(Coordinate cdn) {
    	int[] oneWideSpaces = new int[4];
    	int[] twoWideSpaces = new int[8];
    	int possCount = 0;
    	
    	oneWideSpaces[0] = countAdjacent(cdn, "up", null, 1);
    	oneWideSpaces[1] = countAdjacent(cdn, "right", null, 1);
    	oneWideSpaces[2] = countAdjacent(cdn, "down", null, 1);
    	oneWideSpaces[3] = countAdjacent(cdn, "left", null, 1);
    	
    	twoWideSpaces[0] = countAdjacent(cdn, "up", "left", 2);
    	twoWideSpaces[1] = countAdjacent(cdn, "up", "right", 2);
    	twoWideSpaces[2] = countAdjacent(cdn, "right", "up", 2);
    	twoWideSpaces[3] = countAdjacent(cdn, "right", "down", 2);
    	twoWideSpaces[4] = countAdjacent(cdn, "down", "left", 2);
    	twoWideSpaces[5] = countAdjacent(cdn, "down", "right", 2);
    	twoWideSpaces[6] = countAdjacent(cdn, "left", "up", 2);
    	twoWideSpaces[7] = countAdjacent(cdn, "left", "down", 2);
    	
    	// For each coordinate, for each ship, check number of possibilities
    	// Count number of adjacent uninterrupted squares in each 4 directions (max length of ship - 1)
    	// Number of possibilities for horizontal/vertical is (total length - ship length) + 1
    	// Repeat for each ship type
    	
    	for (Integer l: oneWideShips) {
    		if (l != 0) {
    			for (int i = 0; i < oneWideSpaces.length; i++) {
        			if (oneWideSpaces[i] >= l) {
        				oneWideSpaces[i] = l - 1;
        			}
        		}
        		
        		possCount = ((1 + oneWideSpaces[0] + oneWideSpaces[2] - l + 1) > 0) ? (possCount + (1 + oneWideSpaces[0] + oneWideSpaces[2] - l + 1)) : (possCount + 0);
        		possCount = ((1 + oneWideSpaces[1] + oneWideSpaces[3] - l + 1) > 0) ? (possCount + (1 + oneWideSpaces[1] + oneWideSpaces[3] - l + 1)) : (possCount + 0);
    		}
    	}
    	
    	for (Integer l: twoWideShips) {
    		if (l != 0) {
    			for (int i = 0; i < twoWideSpaces.length; i++) {
        			if (twoWideSpaces[i] >= l) {
        				twoWideSpaces[i] = l - 1;
        			}
        		}
        		
        		possCount = ((1 + twoWideSpaces[0] + twoWideSpaces[4] - l + 1) > 0) ? (possCount + (1 + twoWideSpaces[0] + twoWideSpaces[4] - l + 1)) : (possCount + 0);
        		possCount = ((1 + twoWideSpaces[1] + twoWideSpaces[5] - l + 1) > 0) ? (possCount + (1 + twoWideSpaces[1] + twoWideSpaces[5] - l + 1)) : (possCount + 0);
        		possCount = ((1 + twoWideSpaces[2] + twoWideSpaces[6] - l + 1) > 0) ? (possCount + (1 + twoWideSpaces[2] + twoWideSpaces[6] - l + 1)) : (possCount + 0);
        		possCount = ((1 + twoWideSpaces[3] + twoWideSpaces[7] - l + 1) > 0) ? (possCount + (1 + twoWideSpaces[3] + twoWideSpaces[7] - l + 1)) : (possCount + 0);
    		}
    	}
    	
    	return possCount;
    }
    
    public int countAdjacent(Coordinate cdn, String directionPrim, String directionSec, Integer width) {
        Coordinate neighbour;
        Coordinate neighbourTwo;
        
     // Define neighbour coordinate based on direction
        switch (directionPrim) {
        case "up":
        	neighbour = new Coordinate(cdn.row-1, cdn.column);
        	break;
        case "right":
        	neighbour = new Coordinate(cdn.row, cdn.column+1);
        	break;
        case "down":
        	neighbour = new Coordinate(cdn.row+1, cdn.column);
        	break;
        case "left":
        	neighbour = new Coordinate(cdn.row, cdn.column-1);
        	break;
        default:
        	return 0;
        }
        
        if (width == 1) {
        	// Check that neighbour is inside  the map and
            // is not a MISSED answer
        	if (neighbour.column>=0 && neighbour.column< world.numColumn 
            		&& neighbour.row>=0 && neighbour.row< world.numRow
            		&& !missedCdnHist.contains(neighbour)) {
            	// Check that neighbour is not part of a sunken ship
            	for (World.ShipLocation sl: world.shipLocations) {
            		if (hitsReceived.get(sl.ship).size() == sl.coordinates.size()
            				&& sl.coordinates.contains(neighbour)) {
            			return 0;
            		}
            	}
            	return 1 + countAdjacent(neighbour, directionPrim, directionSec, width);
            }
            return 0;
        } else if (width == 2) {
        	// Ship is two wide, need to check each neighbour in two-wide rectangle
        	switch (directionPrim) {
        	case "up":
        		switch (directionSec) {
                case "right":
                	// Up, right
                	neighbourTwo = new Coordinate(cdn.row-1, cdn.column+1);
                	break;
                case "left":
                	// Up, left
                	neighbourTwo = new Coordinate(cdn.row-1, cdn.column-1);
                	break;
                default:
                	return 0;
                }
        		break;
        	case "right":
        		switch (directionSec) {
                case "up":
                	// Right, up
                	neighbourTwo = new Coordinate(cdn.row-1, cdn.column+1);
                	break;
                case "down":
                	// Right down
                	neighbourTwo = new Coordinate(cdn.row+1, cdn.column+1);
                	break;
                default:
                	return 0;
                }
        		break;
        	case "down":
        		switch (directionSec) {
                case "right":
                	// Down, right
                	neighbourTwo = new Coordinate(cdn.row+1, cdn.column+1);
                	break;
                case "left":
                	// Down, left
                	neighbourTwo = new Coordinate(cdn.row+1, cdn.column-1);
                	break;
                default:
                	return 0;
                }
        		break;
        	case "left":
        		switch (directionSec) {
                case "up":
                	// Left, up
                	neighbourTwo = new Coordinate(cdn.row-1, cdn.column-1);
                	break;
                case "down":
                	// Left, down
                	neighbourTwo = new Coordinate(cdn.row+1, cdn.column-1);
                	break;
                default:
                	return 0;
                }
        		break;
        	default:
        		return 0;
        	}
        	if (neighbour.column>=0 && neighbour.column< world.numColumn 
            		&& neighbour.row>=0 && neighbour.row< world.numRow
            		&& neighbourTwo.column>=0 && neighbourTwo.column< world.numColumn 
            		&& neighbourTwo.row>=0 && neighbourTwo.row< world.numRow
            		&& !missedCdnHist.contains(neighbour)
            		&& !missedCdnHist.contains(neighbourTwo)) {
            	// Check that neighbour is not part of a sunken ship
            	for (World.ShipLocation sl: world.shipLocations) {
            		if (hitsReceived.get(sl.ship).size() == sl.coordinates.size()
            				&& (sl.coordinates.contains(neighbour)
            				|| sl.coordinates.contains(neighbourTwo))) {
            			return 0;
            		}
            	}
            	return 1 + countAdjacent(neighbour, directionPrim, directionSec, width);
            }
            return 0;
        }
        return 0;
    }

    public Guess makeGuess() {
        Coordinate cdn;
        
        if (targetingMode) {
        	cdn = getHighestPoss(targetingCdn);
        } else {
        	cdn = getHighestPoss(huntingCdn);
        }

        guessCdnHist.add(cdn);
        return cdn.toGuess();
        
    } // end of makeGuess()

    public void update(Guess guess, Answer answer) {
    	Coordinate cdn = new Coordinate(guess);
    	
    	if (answer.isHit) {
    		if (answer.shipSunk != null) {
    			switch (answer.shipSunk.name()) {
    			case "Frigate":
    				oneWideShips[0] = 0;
    				break;
    			case "Submarine":
    				oneWideShips[1] = 0;
    				break;
    			case "PatrolCraft":
    				oneWideShips[2] = 0;
    				break;
    			case "AircraftCarrier":
    				twoWideShips[0] = 0;
    				break;
    			case "Cruiser":
    				twoWideShips[1] = 0;
    				break;
    			}
    			targetingMode = false;
    			huntingCdn.addAll(targetingCdn);
    			targetingCdn.clear();
    			updatePoss(guess, answer);
    		} else {
    			targetingMode = true;
        		addTargets(guess);
    		}
    	} else {
    		missedCdnHist.add(cdn);
    		updatePoss(guess, answer);
    	}
    	
    	if (targetingMode && !(targetingCdn.size() > 0)) {
    		targetingMode = false;
    	}
    } // end of update()
    
    public int lookupPoss(Coordinate cdn) {
    	return possibilities[cdn.row][cdn.column];
    }

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

} // end of class ProbabilisticGuessPlayer
