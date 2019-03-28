package eu.europa.esig.dss.x509.revocation.crl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.europa.esig.dss.x509.RevocationOrigin;
import eu.europa.esig.dss.x509.revocation.SignatureRevocationSource;

@SuppressWarnings("serial")
public abstract class SignatureCRLSource extends OfflineCRLSource implements SignatureRevocationSource<CRLToken> {
	
	Map<CRLBinary, List<CRLToken>> crlTokenMap = new HashMap<CRLBinary, List<CRLToken>>();
	
	private List<CRLToken> revocationValuesCRLs = new ArrayList<CRLToken>();
	private List<CRLToken> attributeRevocationValuesCRLs = new ArrayList<CRLToken>();
	private List<CRLToken> timestampRevocationValuesCRLs = new ArrayList<CRLToken>();
	private List<CRLToken> dssDictionaryCRLs = new ArrayList<CRLToken>();
	private List<CRLToken> vriDictionaryCRLs = new ArrayList<CRLToken>();
	
	private List<CRLRef> completeRevocationRefsCRLs = new ArrayList<CRLRef>();
	private List<CRLRef> attributeRevocationRefsCRLs = new ArrayList<CRLRef>();

	@Override
	public List<CRLToken> getRevocationValuesTokens() {
		removeDuplicates(revocationValuesCRLs);
		return revocationValuesCRLs;
	}

	@Override
	public List<CRLToken> getAttributeRevocationValuesTokens() {
		removeDuplicates(attributeRevocationValuesCRLs);
		return attributeRevocationValuesCRLs;
	}

	@Override
	public List<CRLToken> getTimestampRevocationValuesTokens() {
		removeDuplicates(timestampRevocationValuesCRLs);
		return timestampRevocationValuesCRLs;
	}

	@Override
	public List<CRLToken> getDSSDictionaryTokens() {
		removeDuplicates(dssDictionaryCRLs);
		return dssDictionaryCRLs;
	}

	@Override
	public List<CRLToken> getVRIDictionaryTokens() {
		removeDuplicates(vriDictionaryCRLs);
		return vriDictionaryCRLs;
	}

	public List<CRLRef> getCompleteRevocationRefs() {
		return completeRevocationRefsCRLs;
	}

	public List<CRLRef> getAttributeRevocationRefs() {
		return attributeRevocationRefsCRLs;
	}
	
	public Map<CRLBinary, List<CRLToken>> getCRLTokenMap() {
		return crlTokenMap;
	}
	
	/**
	 * Allows to fill all CRL missing revocation tokens from the given {@link SignatureCRLSource}
	 * @param signatureCRLSource {@link SignatureCRLSource} to populate values from
	 */
	public void populateCRLRevocationTokenLists(SignatureCRLSource signatureCRLSource) {
		Map<CRLBinary, List<CRLToken>> mapToPopulateValuesFrom = signatureCRLSource.getCRLTokenMap();
		for (Entry<CRLBinary, List<CRLToken>> entry : mapToPopulateValuesFrom.entrySet()) {
			for (CRLToken crlToken : entry.getValue()) {
				storeCRLToken(entry.getKey(), crlToken);
			}
		}
	}
	
	@Override
	protected void storeCRLToken(CRLBinary crlBinary, CRLToken crlToken) {
		if (crlsBinaryList.contains(crlBinary)) {
			if (!crlTokenMap.containsKey(crlBinary)) {
				crlTokenMap.put(crlBinary, new ArrayList<CRLToken>(Collections.singletonList(crlToken)));
				addToRelevantList(crlToken, crlBinary.getOrigin());
			} else if (!crlTokenMap.get(crlBinary).contains(crlToken)) {
				crlTokenMap.get(crlBinary).add(crlToken);
				addToRelevantList(crlToken, crlBinary.getOrigin());
			}
		}
	}
	
	private void addToRelevantList(CRLToken crlToken, RevocationOrigin origin) {
		switch (origin) {
			case INTERNAL_REVOCATION_VALUES:
				revocationValuesCRLs.add(crlToken);
				break;
			case INTERNAL_ATTRIBUTE_REVOCATION_VALUES:
				attributeRevocationValuesCRLs.add(crlToken);
				break;
			case INTERNAL_TIMESTAMP_REVOCATION_VALUES:
				timestampRevocationValuesCRLs.add(crlToken);
				break;
			case INTERNAL_DSS:
				dssDictionaryCRLs.add(crlToken);
				break;
			case INTERNAL_VRI:
				vriDictionaryCRLs.add(crlToken);
			default:
				break;
		}
	}
	
	protected void addReference(CRLRef crlRef, RevocationOrigin origin) {
		switch (origin) {
		case COMPLETE_REVOCATION_REFS:
			if (!completeRevocationRefsCRLs.contains(crlRef)) {
				completeRevocationRefsCRLs.add(crlRef);
			}
			break;
		case ATTRIBUTE_REVOCATION_REFS:
			if (!attributeRevocationRefsCRLs.contains(crlRef)) {
				attributeRevocationRefsCRLs.add(crlRef);
			}
		default:
			break;
		}
	}
	
	private void removeDuplicates(List<CRLToken> crlTokenList) {
		List<String> usedIds = new ArrayList<String>();
		Iterator<CRLToken> iterator = crlTokenList.iterator();
		while (iterator.hasNext()) {
			CRLToken crlToken = iterator.next();
			String revocationRefId = crlToken.getDSSIdAsString() + crlToken.getRelatedCertificateID();
			if (usedIds.contains(crlToken.getDSSIdAsString() + crlToken.getRelatedCertificateID())) {
				iterator.remove();
			} else {
				usedIds.add(revocationRefId);
			}
		}
	}

}
