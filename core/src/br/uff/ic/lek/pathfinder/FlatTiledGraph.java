package br.uff.ic.lek.pathfinder;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

//import br.uff.ic.lek.pathfinder.DungeonUtils;
//import br.uff.ic.lek.pathfinder.TiledGraph;

/** A random generated graph representing a flat tiled map.
 *
 * @author davebaol */
public class FlatTiledGraph implements TiledGraph<FlatTiledNode> {
    public static int sizeX =  30; // 125; // 200; //100; //LEK nao é mais final
    public static int sizeY =  30; // 75; // 120; //60; //LEK

    protected Array<FlatTiledNode> nodes;

    public boolean diagonal;
    public FlatTiledNode startNode;


    public FlatTiledGraph () {
        this.nodes = new Array<FlatTiledNode>(sizeX * sizeY);
        this.diagonal = false;
        this.startNode = null;
    }

	/*
	 * LEK
	 * TO DO
	 * fazer um metodo static para substituir DungeonUtils.generate
	 * int map[][] = DungeonUtils.generate
	 *
	 * Esse método deverá varrer o TiledMap original e onde tiver os tileds de caminho (pedras de calcamento) marcar como 1 ou TILE_FLOOR, as bordas e os demais como TILE_WALL
	 * o render passa a ser o render de TiledMap. Para testes voce podera ver o desenho do labirinto na forma de quadrados cinzas, brancos e vermelhos(linha)
	 */
    public FlatTiledGraph (int sX, int sY) { // LEK adicionado
        this.nodes = new Array<FlatTiledNode>(sX * sY);
        FlatTiledGraph.sizeX = sX;
        FlatTiledGraph.sizeY = sY;
        this.diagonal = false;
        this.startNode = null;
    }

    @Override
    public void init (int map[][]) {
        //LEK	int map[][] = DungeonUtils.generate(sizeX, sizeY, roomCount, roomMinSize, roomMaxSize, squashIterations);
        for (int x = 0; x < FlatTiledGraph.sizeX; x++) {
            for (int y = 0; y < FlatTiledGraph.sizeY; y++) {
                if (x==0 || y==0 || x== FlatTiledGraph.sizeX-1 || y== FlatTiledGraph.sizeY-1) // LEK
                    map[x][y] = 0; // limites do map com 0
                nodes.add(new FlatTiledNode(x, y, map[x][y], 4));
            }
        }
        Gdx.app.log("Path ", " FlatTiledGraph.sizeX="+FlatTiledGraph.sizeX + " FlatTiledGraph.sizeY="+FlatTiledGraph.sizeY);
        //*
        // Each node has up to 4 neighbors, therefore no diagonal movement is possible
        for (int x = 0; x < FlatTiledGraph.sizeX; x++) {
            int idx = x * FlatTiledGraph.sizeY;
            for (int y = 0; y < FlatTiledGraph.sizeY; y++) {
                FlatTiledNode n = nodes.get(idx + y);
                if (x > 0) addConnection(n, -1, 0);
                if (y > 0) addConnection(n, 0, -1);
                if (x < FlatTiledGraph.sizeX - 1) addConnection(n, 1, 0);
                if (y < FlatTiledGraph.sizeY - 1) addConnection(n, 0, 1);
            }
        }
        //*/
    }

    @Override
    public void init (int roomCount, int roomMinSize, int roomMaxSize, int squashIterations) {
        int map[][] = DungeonUtils.generate(sizeX, sizeY, roomCount, roomMinSize, roomMaxSize, squashIterations);
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                nodes.add(new FlatTiledNode(x, y, map[x][y], 4));
            }
        }

        // Each node has up to 4 neighbors, therefore no diagonal movement is possible
        for (int x = 0; x < sizeX; x++) {
            int idx = x * sizeY;
            for (int y = 0; y < sizeY; y++) {
                FlatTiledNode n = nodes.get(idx + y);
                if (x > 0) addConnection(n, -1, 0);
                if (y > 0) addConnection(n, 0, -1);
                if (x < sizeX - 1) addConnection(n, 1, 0);
                if (y < sizeY - 1) addConnection(n, 0, 1);
            }
        }
    }

    @Override
    public FlatTiledNode getNode (int x, int y) {
        return nodes.get(x * sizeY + y);
    }

    @Override
    public FlatTiledNode getNode (int index) {
        return nodes.get(index);
    }

    @Override
    public int getIndex (FlatTiledNode node) {
        return node.getIndex();
    }

    @Override
    public int getNodeCount () {
        return nodes.size;
    }

    @Override
    public Array<Connection<FlatTiledNode>> getConnections (FlatTiledNode fromNode) {
        return fromNode.getConnections();
    }

    private void addConnection (FlatTiledNode n, int xOffset, int yOffset) {
        FlatTiledNode target = getNode(n.x + xOffset, n.y + yOffset);
        if (target.type == FlatTiledNode.TILE_FLOOR) n.getConnections().add(new FlatTiledConnection(this, n, target));
    }

}