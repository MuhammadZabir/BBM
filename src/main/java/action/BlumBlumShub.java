package action;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BlumBlumShub {

    private static final BigInteger two = BigInteger.valueOf(2L);

    private static final BigInteger three = BigInteger.valueOf(3L);

    private static final BigInteger four = BigInteger.valueOf(4L);

    private BigInteger n;

    private BigInteger state;

    private static BigInteger getPrime(int bits, Random rand) {
        BigInteger p;
        while (true) {
            p = new BigInteger(bits, 100, rand);
            if (p.mod(four).equals(three))
                break;
        }
        return p;
    }

    public static BigInteger generateN(int bits, Random rand) {
        BigInteger p = getPrime(bits/2, rand);
        BigInteger q = getPrime(bits/2, rand);

        while (p.equals(q)) {
            q = getPrime(bits, rand);
        }
        return p.multiply(q);
    }

    /**
     * Constructor, specifing bits for <i>n</i>
     *
     * @param bits number of bits
     */
    public BlumBlumShub(int bits) {
        this(bits, new Random());
    }

    /**
     * Constructor, generates prime and seed
     *
     * @param bits
     * @param rand
     */
    public BlumBlumShub(int bits, Random rand) {
        this(generateN(bits, rand));
    }

    /**
     * A constructor to specify the "n-value" to the Blum-Blum-Shub algorithm.
     * The inital seed is computed using Java's internal "true" random number
     * generator.
     *
     * @param n
     *            The n-value.
     */
    public BlumBlumShub(BigInteger n) {
        this(n, SecureRandom.getSeed(n.bitLength() / 8));
    }

    /**
     * A constructor to specify both the n-value and the seed to the
     * Blum-Blum-Shub algorithm.
     *
     * @param n
     *            The n-value using a BigInteger
     * @param seed
     *            The seed value using a byte[] array.
     */
    public BlumBlumShub(BigInteger n, byte[] seed) {
        this.n = n;
        setSeed(seed);
    }

    /**
     * Sets or resets the seed value and internal state
     *
     * @param seedBytes
     *            The new seed.
     */
    public void setSeed(byte[] seedBytes) {
        // ADD: use hardwired default for n
        BigInteger seed = new BigInteger(1, seedBytes);
        state = seed.mod(n);
    }

    /**
     * Returns up to numBit random bits
     *
     * @return int
     */
    public String next(int numBits) {
        int result = 0;
        for (int i = numBits; i != 0; --i) {
            state = state.modPow(two, n);
            result = (result << 1) | (state.testBit(0) == true ? 1 : 0);
        }
        return Integer.toHexString(result);
    }

    /**
     * A quickie test application for BlumBlumShub.
     */
    public static List<String> randomText(StringBuffer content) {

        SecureRandom r = new SecureRandom();
        content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                ": Generating stock random seed" + System.lineSeparator());
        r.nextInt();

        content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                ": Generating N" + System.lineSeparator());
        int bitsize = 64;
        BigInteger nval = BlumBlumShub.generateN(bitsize, r);

        content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                ": Generated N = " + nval + System.lineSeparator());
        byte[] seed = new byte[bitsize/8];
        r.nextBytes(seed);

        BlumBlumShub bbs = new BlumBlumShub(nval, seed);

        content.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")) +
                ": Generating 10 bytes" + System.lineSeparator());
        List<String> integers = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            integers.add(bbs.next(64));
        }

        return integers;
    }
}
