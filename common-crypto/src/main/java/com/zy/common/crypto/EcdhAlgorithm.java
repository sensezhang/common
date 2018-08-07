/*===================================================================
 * 北京深思数盾科技有限公司
 * 日期：2015年8月17日 下午4:55:39
 * 作者：jiangtao
 * 版本：1.0.0
 * 版权：All rights reserved.
 *===================================================================
 * 修订日期           修订人               描述
 * 2015年8月17日     jiangtao	      创建
 */
package com.zy.common.crypto;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import javax.crypto.KeyAgreement;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.security.ec.ECPublicKeyImpl;

/**
 * @author jiangt
 */
public class EcdhAlgorithm {
  
  public static final String ALGORITHM = "ECDH";
  private static final String PROVIDER = "BC";
  private static ECParameterSpec ecParameterSpec;
  public static KeyPairGenerator keyGen;
  static {
    Security.addProvider(new BouncyCastleProvider());
    ecParameterSpec = ecPar();
    try {
      keyGen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
      keyGen.initialize(getEcPar(), new SecureRandom());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (NoSuchProviderException e) {
      throw new RuntimeException(e);
    } catch (InvalidAlgorithmParameterException e) {
      throw new RuntimeException(e);
    }
  }
  
  public static EcdhResult generateSecret(ECPoint point) throws Exception {
    
    KeyAgreement aKeyAgree = KeyAgreement.getInstance(ALGORITHM, PROVIDER);
    
    KeyPair aPair = getKeyPair();
    aKeyAgree.init(aPair.getPrivate());
    
    ECPublicKey clientPublicKey = new ECPublicKeyImpl(point, getEcPar());
    aKeyAgree.doPhase(clientPublicKey, true);
    
    byte[] secret = aKeyAgree.generateSecret();
    
    ECPublicKey ecPublicKey = (ECPublicKey) aPair.getPublic();
    
    ECPoint serverPoint = ecPublicKey.getW();
    
    return new EcdhResult(secret, serverPoint);
    
  }
  
  /**
   * @return
   * @throws NoSuchProviderException
   * @throws NoSuchAlgorithmException
   * @throws InvalidAlgorithmParameterException
   */
  public static KeyPair getKeyPair() {
    return keyGen.generateKeyPair();
  }
  
  public static ECParameterSpec getEcPar() {
    if (ecParameterSpec == null) {
      ecParameterSpec = ecPar();
    }
    return ecParameterSpec;
    
  }
  
  private static ECParameterSpec ecPar() {
    ECPoint g = new ECPoint(new BigInteger("188DA80EB03090F67CBF20EB43A18800F4FF0AFD82FF1012", 16),
        new BigInteger("07192B95FFC8DA78631011ED6B24CDD573F977A11E794811", 16));
    EllipticCurve curve = new EllipticCurve(
        new ECFieldFp(new BigInteger("fffffffffffffffffffffffffffffffeffffffffffffffff", 16)), // p
        new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFC", 16), // a
        new BigInteger("64210519E59C80E70FA7E9AB72243049FEB8DEECC146B9B1", 16)); // b
    
    return new ECParameterSpec(curve, g, // G
        new BigInteger("ffffffffffffffffffffffff99def836146bc9b1b4d22831", 16), // order
        1);
  }
}
