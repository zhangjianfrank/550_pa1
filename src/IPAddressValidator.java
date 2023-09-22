import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPAddressValidator {
    private static final String IPV4_PATTERN =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    private static final String IPV6_PATTERN =
            "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";

    private static final Pattern IPV4_PATTERN_REGEX = Pattern.compile(IPV4_PATTERN);
    private static final Pattern IPV6_PATTERN_REGEX = Pattern.compile(IPV6_PATTERN);

    public static boolean isValidIPv4(String ipAddress) {
        Matcher matcher = IPV4_PATTERN_REGEX.matcher(ipAddress);
        return matcher.matches();
    }

    public static boolean isValidIPv6(String ipAddress) {
        Matcher matcher = IPV6_PATTERN_REGEX.matcher(ipAddress);
        return matcher.matches();
    }

    public static boolean isValidIP(String ipAddress) {
        return isValidIPv4(ipAddress) || isValidIPv6(ipAddress);
    }

    public static void main(String[] args) {
        String ipAddress1 = "192.168.1.1";
        String ipAddress2 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
        String ipAddress3 = "invalid-ip";

        System.out.println("Is Valid IPv4? " + isValidIPv4(ipAddress1));
        System.out.println("Is Valid IPv6? " + isValidIPv6(ipAddress2));
        System.out.println("Is Valid IP? " + isValidIP(ipAddress3));
    }
}
