package www0abdb.oss.authiva.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void hashShouldVerifyCorrectPassword() {
        String password = "TestPassword123!";
        String hash = PasswordHasher.hash(password);

        assertTrue(PasswordHasher.verify(password, hash));
    }

    @Test
    void hashShouldRejectWrongPassword() {
        String hash = PasswordHasher.hash("CorrectPassword123!");

        assertFalse(
                PasswordHasher.verify(
                        "WrongPassword123!",
                        hash
                )
        );
    }

    @Test
    void hashingSamePasswordShouldProduceDifferentHashes() {
        String password = "SamePassword123!";

        String firstHash = PasswordHasher.hash(password);
        String secondHash = PasswordHasher.hash(password);

        assertNotEquals(firstHash, secondHash);

        assertTrue(PasswordHasher.verify(password, firstHash));
        assertTrue(PasswordHasher.verify(password, secondHash));
    }

    @Test
    void invalidHashShouldReturnFalse() {
        assertFalse(
                PasswordHasher.verify(
                        "password",
                        "invalid-hash"
                )
        );
    }

    @Test
    void hashShouldContainExpectedFormat() {
        String hash = PasswordHasher.hash("FormatTest123!");

        String[] parts = hash.split("\\$", 4);

        assertEquals(3, parts.length);
        assertEquals("sha256", parts[0]);
        assertFalse(parts[1].isBlank());
        assertFalse(parts[2].isBlank());
    }
@Test
void measurePasswordHashPerformance() {
    String password = "PerformanceTest123!";
    int runs = 3;

    long totalHashTime = 0;
    long totalVerifyTime = 0;

    for (int i = 0; i < runs; i++) {
        long hashStart = System.nanoTime();

        String hash = PasswordHasher.hash(password);

        totalHashTime += System.nanoTime() - hashStart;

        long verifyStart = System.nanoTime();

        boolean verified = PasswordHasher.verify(password, hash);

        totalVerifyTime += System.nanoTime() - verifyStart;

        assertTrue(verified);
    }

    long averageHashMs =
            totalHashTime / runs / 1_000_000;

    long averageVerifyMs =
            totalVerifyTime / runs / 1_000_000;

    System.out.printf(
            "PasswordHasher benchmark: runs=%d, hash_avg=%d ms, verify_avg=%d ms%n",
            runs,
            averageHashMs,
            averageVerifyMs
    );
}
}
