package WishPool.Be.util;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

// 16자 랜덤 URL 생성기
public final class IdentifierGenerator {
    private IdentifierGenerator() {}

    public static String generateShareIdentifier() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());

        return Base64.getUrlEncoder().withoutPadding().encodeToString(byteBuffer.array());
    }
}
