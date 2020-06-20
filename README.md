# PathFindingVisualiser

## About
This is a grid path-finding visualiser built in Java, utilising [Processing](https://github.com/processing/processing) for GUI rendering.
It utilises [my own path-finding library](https://github.com/danielbatchford/PathFindingVisualiser) in order to 
predict paths through the grid whenever neccessary. 

This visualisation allows various path-finding algorithms to be displayed. Currently, the following algorithms
are implemented:
- A* Search
- Djikstra Search
- Breadth First Search
- Depth First Search

Barriers (un-walkable tiles) can be added and removed from the screen by dragging the mouse over a tile
and the start and goal node can also be moved in the same way.
## How To Run
To do
## Controls
  - `M` to toggle the menu
  - `D` to toggle diagonal movement
  - `P` to toggle pause
  - `R` to randomise start and goal positions
  - `C` to clear the board
  - Use the mouse to place obstacles and move the start and end locations 
  - Use number keys 1-4 to select different searching algorithms
              
## Sample Simulation
![alt text](https://github.com/danielbatchford/PathFindingVisualiser/blob/master/sample.gif)


## How it works
Paths are calculated using my own [path-finding library](https://github.com/danielbatchford/PathFindingVisualiser), which calculates paths across a grid.
When the state of the grid changes (clearing the grid, marking squares unwalkable, changing search type etc), a new path is calculated.

Since paths in this visualisation are computed before any screen update occurs, a logger built into the pathfinding library
records open and closed list states at each state, which can be rendered to the screen in stages. Once this rendering completes,
the computed path from before is shown.
