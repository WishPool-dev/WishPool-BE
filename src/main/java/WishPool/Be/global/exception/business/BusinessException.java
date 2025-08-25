package WishPool.Be.global.exception.business;


import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private final ErrorStatus errorStatus;
    public BusinessException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }
}
