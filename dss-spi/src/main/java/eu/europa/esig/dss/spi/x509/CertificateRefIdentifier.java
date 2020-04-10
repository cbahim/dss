package eu.europa.esig.dss.spi.x509;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.model.identifier.Identifier;
import eu.europa.esig.dss.spi.DSSUtils;

public class CertificateRefIdentifier extends Identifier {

	private static final long serialVersionUID = -957484858420900350L;
	
	private static final DigestAlgorithm SKI_DIGEST_ALGO = DigestAlgorithm.SHA1; // by RFC 6960

	protected CertificateRefIdentifier(CertificateRef certificateRef) {
		super("C-", getDigest(certificateRef));
	}
	
	private static Digest getDigest(CertificateRef certificateRef) {
		Digest certDigest = certificateRef.getCertDigest();
		if (certDigest != null) {
			return certDigest;
		}
		CertificateIdentifier certificateIdentifier = certificateRef.getCertificateIdentifier();
		if (certificateIdentifier != null) {
			if (certificateIdentifier.getSki() != null) {
				return new Digest(SKI_DIGEST_ALGO, certificateIdentifier.getSki());
			}
			byte[] issuerSerialEncoded = certificateIdentifier.getIssuerSerialEncoded();
			if (issuerSerialEncoded != null) {
				return new Digest(DIGEST_ALGO, DSSUtils.digest(DIGEST_ALGO, issuerSerialEncoded));
			}
			if (certificateIdentifier.getIssuerName() != null) {
				return new Digest(DIGEST_ALGO, DSSUtils.digest(DIGEST_ALGO, certificateIdentifier.getIssuerName().getEncoded()));
			} 
		}
		ResponderId responderId = certificateRef.getResponderId();
		if (responderId != null) {
			if (responderId.getSki() != null) {
				return new Digest(SKI_DIGEST_ALGO, responderId.getSki());
			}
			if (responderId.getX500Principal() != null) {
				return new Digest(DIGEST_ALGO, DSSUtils.digest(DIGEST_ALGO, responderId.getX500Principal().getEncoded()));
			}
		}
		throw new DSSException("One of [certDigest, publicKeyDigest, issuerInfo] must be defined for a CertificateRef!");
	}
	
	

}