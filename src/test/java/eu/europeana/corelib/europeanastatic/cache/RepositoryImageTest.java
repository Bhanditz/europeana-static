package eu.europeana.corelib.europeanastatic.cache;

import java.net.URLDecoder;

import org.junit.Test;

public class RepositoryImageTest {


	@Test
	public void test(){
		RepositoryImpl rep = new RepositoryImpl();
		String hash = rep.createHash("http://oai-pmh.nid.pl:5070/nid-ws/rest/oaipmh/getImgMin?url=carare/Pomniki_Historii/Katowice_-_Gmach_Województwa_i_Sejmu_Śląskiego/NID-pomhis-Katowice_Gmach-003.jpg");
		System.out.println(hash);
	}
}
