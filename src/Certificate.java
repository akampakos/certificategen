import sun.misc.BASE64Encoder;
import sun.security.provider.X509Factory;
import sun.security.x509.*;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.util.Date;

public class Certificate {


    /**
     * Create a self-signed X.509 Certificate
     *
     * @param pair      the KeyPair
     * @param dn        the X.509 Distinguished Name, eg "CN=Test, L=Athens, C=GB"
     * @param days      how many days from now the Certificate is valid for
     * @param algorithm the signing algorithm, eg "SHA1withRSA"
     */
    String generateCertificate(String dn, int days, String algorithm, int keysize)
            throws GeneralSecurityException, IOException {
        KeyPair pair = generateKeyPair(keysize);

        PrivateKey privkey = pair.getPrivate();
        PublicKey pubkey = pair.getPublic();
        X509CertInfo info = new X509CertInfo();
        Date from = new Date();
        Date to = new Date(from.getTime() + days * 86400000l);
        CertificateValidity interval = new CertificateValidity(from, to);
        BigInteger sn = new BigInteger(64, new SecureRandom());
        X500Name owner = new X500Name(dn);

        info.set(X509CertInfo.VALIDITY, interval);
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
        info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
        info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
        info.set(X509CertInfo.KEY, new CertificateX509Key(pair.getPublic()));
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));

        // Sign the cert to identify the algorithm that's used.
        X509CertImpl cert = new X509CertImpl(info);
        cert.sign(privkey, algorithm);

        // Update the algorith, and resign.
        algo = (AlgorithmId) cert.get(X509CertImpl.SIG_ALG);
        info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
        cert = new X509CertImpl(info);
        cert.sign(privkey, algorithm);

        BASE64Encoder encoder = new BASE64Encoder();
        return X509Factory.BEGIN_CERT + "\n" + encoder.encodeBuffer(cert.getEncoded()) + X509Factory.END_CERT + "\n\n\n" + "-------------------------------- Private Key -------------------------------" + "\n" + encoder.encodeBuffer(privkey.getEncoded()) + "\n\n\n" + "--------------------------------- Public Key -------------------------------" + "\n" + encoder.encodeBuffer(pubkey.getEncoded());
    }

    public static KeyPair generateKeyPair(Integer keySize) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

        keyGen.initialize(keySize, random);
        KeyPair generateKeyPair = keyGen.generateKeyPair();
        return generateKeyPair;
    }


}
