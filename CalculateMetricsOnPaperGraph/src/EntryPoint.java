import neo4jHelper.CalculatePageRank;
import neo4jHelper.UnderstandDatabase;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;


public class EntryPoint {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UnderstandDatabase ud = new UnderstandDatabase("/var/lib/datasets/rawdata/relatedwork/db_folder");
//		ud.listProperties(50);
//		ud.countAverageNodeDegrees(50);
		System.out.println("calculate pagerank with 5 iterations");
//		CalculatePageRank cp = new CalculatePageRank(ud.getGraphDB());
//		cp.dcalculatePageRank(0.85, 2);
//		ud.setGraphDB(cp.getGraphDb());
		ud.showPageRankValues();
		ud.shutDown();
		
	}

}
