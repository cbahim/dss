package eu.europa.esig.dss.tsl.predicates.othertslpointer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import eu.europa.esig.trustedlist.TrustedListFacade;
import eu.europa.esig.trustedlist.jaxb.tsl.OtherTSLPointersType;
import eu.europa.esig.trustedlist.jaxb.tsl.TrustStatusListType;

public class OtherTLSLPointerPredicatesTest {

	@Test
	public void test() throws Exception {
		try (FileInputStream fis = new FileInputStream("src/test/resources/eu-lotl.xml")) {
			TrustStatusListType trustStatusListType = TrustedListFacade.newFacade().unmarshall(fis);
			assertNotNull(trustStatusListType);

			OtherTSLPointersType pointersToOtherTSL = trustStatusListType.getSchemeInformation().getPointersToOtherTSL();
			assertNotNull(pointersToOtherTSL);

			assertEquals(1, pointersToOtherTSL.getOtherTSLPointer().stream().filter(new EULOTLOtherTSLPointer()).count());
			assertEquals(43, pointersToOtherTSL.getOtherTSLPointer().stream().filter(new EUTLOtherTSLPointer()).count());

			assertEquals(12, pointersToOtherTSL.getOtherTSLPointer().stream().filter(new PDFOtherTSLPointer()).count());
			assertEquals(32, pointersToOtherTSL.getOtherTSLPointer().stream().filter(new XMLOtherTSLPointer()).count());
			assertEquals(12, pointersToOtherTSL.getOtherTSLPointer().stream().filter(new XMLOtherTSLPointer().negate()).count());

			assertEquals(2, pointersToOtherTSL.getOtherTSLPointer().stream().filter(new SchemeTerritoryOtherTSLPointer("BG")).count());
			assertEquals(1,
					pointersToOtherTSL.getOtherTSLPointer().stream().filter(new SchemeTerritoryOtherTSLPointer("BG").and(new XMLOtherTSLPointer())).count());

			assertEquals(3, pointersToOtherTSL.getOtherTSLPointer().stream()
					.filter(new SchemeTerritoryOtherTSLPointer(new HashSet<String>(Arrays.asList("BG", "CY")))).count());
		}
	}

	@Test
	public void exceptions() {
		assertThrows(NullPointerException.class, () -> new MimetypeOtherTSLPointer(null));
		assertThrows(NullPointerException.class, () -> new TypeOtherTSLPointer(null));
	}

}
