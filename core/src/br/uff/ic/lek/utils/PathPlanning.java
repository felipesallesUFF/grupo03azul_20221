/*
    Fábrica de Software para Educação
    Professor Lauro Kozovits, D.Sc.
    ProfessorKozovits@gmail.com
    Universidade Federal Fluminense, UFF
    Rio de Janeiro, Brasil
    Subprojeto: Alchemie Zwei

    Partes do software registradas no INPI como integrantes de alguns apps para smartphones
    Copyright @ 2016..2022

    Se você deseja usar partes do presente software em seu projeto, por favor mantenha esse cabeçalho e peça autorização de uso.
    If you wish to use parts of this software in your project, please keep this header and ask for authorization to use.

 */
package br.uff.ic.lek.utils;

import br.uff.ic.lek.game.World;
//https://github.com/libgdx/gdx-ai/wiki
import br.uff.ic.lek.pathfinder.FlatTiledGraph;// necessário no projeto
import br.uff.ic.lek.pathfinder.FlatTiledNode;// necessário no projeto
import br.uff.ic.lek.pathfinder.TiledManhattanDistance;// necessário no projeto
import br.uff.ic.lek.pathfinder.TiledRaycastCollisionDetector;// necessário no projeto
import br.uff.ic.lek.pathfinder.TiledSmoothableGraphPath; // necessário no projeto

import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
//import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder.Metrics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;


public class PathPlanning {
    // LEK atributos a seguir usados em PathFinder
    public FlatTiledGraph worldMap;
    float tileWidth = 32; // 5; // 10;
    float tileHeight = 32; // 5; // 10;
    ShapeRenderer renderer;
    Vector3 tmpUnprojection = new Vector3();
    int lastScreenX;
    int lastScreenY;
    int lastEndTileX;
    int lastEndTileY;
    int startTileX;
    int startTileY;
    TiledSmoothableGraphPath<FlatTiledNode> path;
    TiledManhattanDistance<FlatTiledNode> heuristic;
    IndexedAStarPathFinder<FlatTiledNode> pathFinder;
    PathSmoother<FlatTiledNode, Vector2> pathSmoother;
    boolean smooth = false;
    public World world;
    private int[][] pathMap;

    public PathPlanning(World world) { // int xTiles, int yTiles, TiledMap map){
        this.world = world;
    }

    public void create() {
        //Gdx.app.log("PathPlanning ", "Sprite width="+tileWidth);
        //LEK TODO FlatTiledGraph worldMap.init(xTiles, yTiles);
        lastEndTileX = 4;//10, 14
        lastEndTileY = 4;
        lastScreenX = lastEndTileX * (int) tileWidth;
        lastScreenY = lastEndTileY * (int) tileWidth;
        startTileX = 1;
        startTileY = 1;
        // Create the map
        worldMap = new FlatTiledGraph(World.xTiles, World.yTiles);
        tileWidth = (float) world.map.getProperties().get("tilewidth", Integer.class);
        tileHeight = (float) world.map.getProperties().get("tileheight", Integer.class);
        pathMap = new int[World.xTiles][World.yTiles];


        int maiorValorEncontrado = 0, min = 1, max = 100 * World.xTiles * World.yTiles;
        // pensar nesse max, pois a posicao ao longo de x,y vai avancando dado o número de sorteios
        // até encontrar uma posicao candidata com valor bem alto com chance de 1% de mudar (na prática o valor 100)
        int avatarX = 0;
        int avatarY = 0;
        for (int i = 0; i < World.xTiles; i++) {
            for (int j = 0; j < World.yTiles; j++) {
                TiledMapTileLayer cur = (TiledMapTileLayer) world.map.getLayers().get("path01"); //.get("map88"); //.get("Path");
                //Cell cell = new Cell();
                if (cur.getCell(i, j) != null) {
                    //LEK TODO adicionar o retângulo no mapa do pathfinder como área de  colisão worldMap.set(i,j, value)
                    cur.setVisible(true); // LEK mostra ou não os caminhos possiveis em verde
                    pathMap[i][j] = 1;
                    int num = RandomNumber.random(min, max);
                    if (num > maiorValorEncontrado) {
                        maiorValorEncontrado = num;
                        avatarX = i;  // posição no path
                        avatarY = j;
                        System.out.println("maior valor até agora=" + maiorValorEncontrado + " posX=" + avatarX + " posY=" + avatarY);
                    }
                } else {
                    pathMap[i][j] = 0; // o valor 0 correspondente aos limites do mapa é colocado automaticamente dentro do método .init a seguir
                } //LEK TODO sortear a primeira posição do avatar no mapa atribuindo um número aleatório a cada posição e substituindo por nova somente se o número sorteado [0..32000] for maior
            }
        }
        // poderia ser uma posicao na entrada de uma caverna
        worldMap.init(pathMap);


        avatarX = 0;
        avatarY = 0;
        maiorValorEncontrado = 0;
        for (int i = 0; i < World.xTiles; i++) {
            for (int j = 0; j < World.yTiles; j++) {
                TiledMapTileLayer cur = (TiledMapTileLayer) world.map.getLayers().get("cavernas");
                if (cur.getCell(i, j) != null) {
                    cur.setVisible(true);
                    int num = RandomNumber.random(min, max);
                    if (num > maiorValorEncontrado) {
                        maiorValorEncontrado = num;
                        avatarX = i-1;// posicao base do avatar é canto inferior esquerdo
                        avatarY = j-1;
                        System.out.println("maior valor de caverna até agora=" + maiorValorEncontrado + " posX=" + avatarX + " posY=" + avatarY);
                    }
                }
            }
        }


        World.avatarStartTileX = avatarX;
        World.avatarStartTileY = avatarY;

        path = new TiledSmoothableGraphPath<>();//<FlatTiledNode>
        heuristic = new TiledManhattanDistance<>();//<FlatTiledNode>
        pathFinder = new IndexedAStarPathFinder<>(worldMap, true);
        pathSmoother = new PathSmoother<>(new TiledRaycastCollisionDetector<>(worldMap));//<FlatTiledNode, Vector2> <FlatTiledNode>
        renderer = new ShapeRenderer();
        //opcoes de controle do Path: usar algum comando para trocar
        smooth = true;//		updatePath(true);
        worldMap.diagonal = true; //		updatePath(true);

        // Para exibir as estatisticas de tempo gasto com PathPlanning
        //pathFinder.metrics = false ? new Metrics() : null; 		updatePath(true);
        //fim das opcoes de controle do Path:
    }

    /*
     * Quando um ator c é colocado no mapa aleatoriamente, a região sorteada pode não ter um ID válido para
     * permitir o caminhamento. Esse algoritmo visa obter uma regiao valida dentro do path para colocar o ator
     * Torna-se necessário  aumentar um nível de tiles em torno da região procurada
     * Ex:
     *    ? ? ?
     *    ? c ?   -->   passo 1
     *    ? ? ?
     *
     *    ? ? ? ? ?
     *    ? 1 1 1 ?
     *    ? 1 c 1 ? --> passo 2
     *    ? 1 1 1 ?
     *    ? ? ? ? ?
     *
     *    Naturalmente se, por exemplo c estiver na posição 0,0 e o mapa tiver 100x100 (World.xTiles=100 e World.yTiles=100)
     *    o algoritmo vai pesquisar no máximo entre -100 e 100 descartando elementos fora do mapa
     *    Como o algoritmo procura sempre da esquerda para a direita e de baixo para cima (y cresce) o elemento com id desejado mais próximo
     *    seguira essa orientação
     */
    public boolean getClosestTileWithID(TileCell1 closest) {
        if (closest.u < 0 || closest.u >= World.xTiles) return false;
        if (closest.v < 0 || closest.v >= World.yTiles) return false;


        //System.out.println("-----------------------------"+" procura ("+closest.u+","+closest.v+") ");

        if (pathMap[closest.u][closest.v] == closest.id) {
            //System.out.println(" achou ("+closest.u+","+closest.v+") ");
            return true; //
        }
        int passo = 1;
        while (passo <= World.xTiles || passo <= World.yTiles) {
			/* o Algoritmo comentado eh mais simples, mas mais ineficiente por testar o que já foi testado
			for(int v=closest.v-passo; v<=closest.v+passo; v++){
				if(v<0 || v>= World.yTiles)	continue;
				for(int u=closest.u-passo;u<=closest.u+passo; u++){
					if(u<0 || u>= World.xTiles)	continue;
					//System.out.print("("+u+","+v+") ");
					if(pathMap[u][v] == closest.id){
						closest.u = u;
						closest.v = v;
						//System.out.println(" achou ("+u+","+v+") ");
						return true; //
					}
				}
				//System.out.println(" passo"+passo);
			}
			*/
            //System.out.println(passo+"linha vertical x menor");
            for (int v = closest.v - passo; v <= closest.v + passo; v++) {
                // varia na vertical, horizontal constante
                if (v < 0 || v >= World.yTiles) continue;
                int u = closest.u - passo;
                if (u < 0) break;
                //System.out.println("("+u+","+v+") passo"+passo);
                if (pathMap[u][v] == closest.id) {
                    closest.u = u;
                    closest.v = v;
                    //System.out.println(" achou ("+u+","+v+") ");
                    return true; //
                }
            }
            //System.out.println(passo+"linha vertical x maior");
            for (int v = closest.v - passo; v <= closest.v + passo; v++) {
                if (v < 0 || v >= World.yTiles) continue;
                int u = closest.u + passo;
                if (u >= World.xTiles) break;
                //System.out.println("("+u+","+v+") passo"+passo);
                if (pathMap[u][v] == closest.id) {
                    closest.u = u;
                    closest.v = v;
                    //System.out.println(" achou ("+u+","+v+") ");
                    return true; //
                }
            }
            //System.out.println(passo+"linha horizontal y menor");
            for (int u = closest.u - passo; u <= closest.u + passo; u++) {
                if (u < 0 || u >= World.xTiles) continue;
                int v = closest.v - passo;
                if (v < 0) break;
                //System.out.println("("+u+","+v+") passo"+passo);
                if (pathMap[u][v] == closest.id) {
                    closest.u = u;
                    closest.v = v;
                    //System.out.println(" achou ("+u+","+v+") ");
                    return true; //
                }
            }
            //System.out.println(passo+"linha horizontal y maior");
            for (int u = closest.u - passo; u <= closest.u + passo; u++) {
                if (u < 0 || u >= World.xTiles) continue;
                int v = closest.v + passo;
                if (v >= World.yTiles) break;
                //System.out.println("("+u+","+v+") passo"+passo);
                if (pathMap[u][v] == closest.id) {
                    closest.u = u;
                    closest.v = v;
                    //System.out.println(" achou ("+u+","+v+") ");
                    return true; //
                }
            }
            passo++;
        }
        return false;
    }

    public void render(float delta) {
        // LEK o render deve ser o TiledMap de sprites e não esse de retângulos pretos, cinzas, brancos e vermelhos(linha)
        renderer.setProjectionMatrix(getCamera().combined);
        renderer.begin(ShapeType.Filled);
        for (int x = 0; x < FlatTiledGraph.sizeX; x++) {
            for (int y = 0; y < FlatTiledGraph.sizeY; y++) {
                switch (worldMap.getNode(x, y).type) {
                    case FlatTiledNode.TILE_FLOOR:
                        renderer.setColor(Color.WHITE);
                        break;
                    case FlatTiledNode.TILE_WALL:
                        renderer.setColor(Color.GRAY);
                        break;
                    default:
                        renderer.setColor(Color.BLACK);
                        break;
                }
                //renderer.rect(x * tileWidth, y * tileWidth, 5, 5); //tileWidth, tileWidth);
            }
        }

        renderer.setColor(Color.MAGENTA); // LEK troquei para mostrar que o código é diferente
        int nodeCount = path.getCount();
        for (int i = 0; i < nodeCount; i++) {
            FlatTiledNode node = path.nodes.get(i);
            renderer.rect(node.x * tileWidth, node.y * tileWidth, tileWidth/2, tileHeight/2);
        }
        //*
        if (smooth) {
            renderer.end();
            renderer.begin(ShapeType.Line);
            float hw = tileWidth / 2f;
            if (nodeCount > 0) {
                FlatTiledNode prevNode = path.nodes.get(0);
                for (int i = 1; i < nodeCount; i++) {
                    FlatTiledNode node = path.nodes.get(i);
                    renderer.line(node.x * tileWidth + hw, node.y * tileWidth + hw, prevNode.x * tileWidth + hw, prevNode.y * tileWidth + hw);
                    prevNode = node;
                }
            }
        }
        //*/
        renderer.end();
    }


    public void dispose() {
        renderer.dispose();
        worldMap = null;
        path = null;
        heuristic = null;
        pathFinder = null;
        pathSmoother = null;
    }

    public Camera getCamera() {
        return world.getCamera();
    }

    private void updatePath(boolean forceUpdate) {

        //WorldController.vec = new Vector3(screenX, screenY, 0);
        //this.camera.unproject(vec);
        //WorldController.target = new Vector3(vec);
        Vector3 vec = new Vector3(lastScreenX, lastScreenY, 0);
        //Gdx.app.log("PathPlanning ", " vec.x="+vec.x+" vec.y="+vec.y);
        getCamera().update();
        getCamera().unproject(vec);
        //Gdx.app.log("PathPlanning ", " vec.x="+vec.x+" vec.y="+vec.y);
        int tileX = (int) (vec.x / tileWidth);
        int tileY = (int) (vec.y / tileWidth);
        if (tileX < 0 || tileX >= World.xTiles || tileY < 0 || tileY >= World.yTiles) return;
        //Gdx.app.log("PathPlanning ", " tileWidth="+tileWidth);
        if (forceUpdate || tileX != lastEndTileX || tileY != lastEndTileY) {

            //Gdx.app.log("PathPlanning ", " lastScreenX="+lastScreenX+" lastScreenY="+lastScreenY+" startTileX="+startTileX+" startTileY="+startTileY+" tileX="+tileX+" tileY="+tileY);
            FlatTiledNode startNode = worldMap.getNode(startTileX, startTileY);
            FlatTiledNode endNode = worldMap.getNode(tileX, tileY);
            if (forceUpdate || endNode.type == FlatTiledNode.TILE_FLOOR) {
                if (endNode.type == FlatTiledNode.TILE_FLOOR) {
                    lastEndTileX = tileX;
                    lastEndTileY = tileY;
                } else {
                    endNode = worldMap.getNode(lastEndTileX, lastEndTileY);
                }
                path.clear();
                worldMap.startNode = startNode;
                long startTime = nanoTime();
                pathFinder.searchNodePath(startNode, endNode, heuristic, path);
                if (pathFinder.metrics != null) {
                    float elapsed = (TimeUtils.nanoTime() - startTime) / 1000000f;
                    System.out.println("----------------- Indexed A* Path Finder Metrics -----------------");
                    System.out.println("Visited nodes................... = " + pathFinder.metrics.visitedNodes);
                    System.out.println("Open list additions............. = " + pathFinder.metrics.openListAdditions);
                    System.out.println("Open list peak.................. = " + pathFinder.metrics.openListPeak);
                    System.out.println("Path finding elapsed time (ms).. = " + elapsed);
                }
                if (smooth) {
                    startTime = nanoTime();
                    pathSmoother.smoothPath(path);
                    if (pathFinder.metrics != null) {
                        float elapsed = (TimeUtils.nanoTime() - startTime) / 1000000f;
                        System.out.println("Path smoothing elapsed time (ms) = " + elapsed);
                    }
                }
            }
        }
    }

    private long nanoTime() {
        return pathFinder.metrics == null ? 0 : TimeUtils.nanoTime();
    }


    public boolean targetChanged(int screenX, int screenY) {
        lastScreenX = screenX;
        lastScreenY = screenY;
        updatePath(true);// LEK

        startTileX = lastEndTileX;
        startTileY = lastEndTileY;
        return true;
    }

}

class TileCell1 {
    public TileCell1(int u, int v, int id) {
        this.u = u;
        this.v = v;
        this.id = id;
    }
    public int id;
    public int u;
    public int v;
}