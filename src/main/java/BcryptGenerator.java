import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptGenerator {
    public static void main(String[] args) {
        // Replace this with the password you want to hash
        String plainPassword = "adminpassword123";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(plainPassword);

        System.out.println("Plain password: " + plainPassword);
        System.out.println("BCrypt hash: " + hashedPassword);
    }
}
