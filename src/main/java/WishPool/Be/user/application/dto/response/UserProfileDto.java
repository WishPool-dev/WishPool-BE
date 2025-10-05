package WishPool.Be.user.application.dto.response;

import WishPool.Be.user.domain.User;

public record UserProfileDto(Long userId, String profileUrl, String nickName)
{
    public static UserProfileDto of(User user){
        return new UserProfileDto(user.getUserId(), user.getProfileImageUrl(), user.getName());
    }
}
