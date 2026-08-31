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
                PasswordHasher.verify("WrongPassword123!", hash)
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
}
