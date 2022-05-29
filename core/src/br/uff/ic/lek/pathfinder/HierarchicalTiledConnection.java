package br.uff.ic.lek.pathfinder;

import com.badlogic.gdx.ai.pfa.DefaultConnection;

/** A connection for a {@link HierarchicalTiledGraph}.
 * 
 * @author davebaol */
public class HierarchicalTiledConnection extends DefaultConnection<HierarchicalTiledNode> {

	static final float NON_DIAGONAL_COST = (float)Math.sqrt(2);

	HierarchicalTiledGraph worldMap;

	public HierarchicalTiledConnection (HierarchicalTiledGraph worldMap, HierarchicalTiledNode fromNode, HierarchicalTiledNode toNode) {
		super(fromNode, toNode);
		this.worldMap = worldMap;
	}

	@Override
	public float getCost () {
		if (worldMap.diagonal) return 1;
		return getToNode().x != worldMap.startNode.x && getToNode().y != worldMap.startNode.y ? NON_DIAGONAL_COST : 1;
	}
}
